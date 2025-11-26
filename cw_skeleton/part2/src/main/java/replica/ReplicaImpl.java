package replica;

import common.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ReplicaImpl extends UnicastRemoteObject implements ReplicatedAuction {

    // ----- sequencing & log state (I suggest you keep these and use them) -----
    private boolean isLeader = false;
    private long lastSeqAssigned = 0; // This is used by the leader to assign a seq number to each Operation      
    private long lastCommitted = 0;           
    private long lastApplied = 0;             
    private final TreeMap<Long, LogEntry> log = new TreeMap<>(); 

    // ----- state machine (in-memory) -----
    // Local auction state for this replica only
    private static class LocalAuctionState {
        final int ownerID;
        final AuctionItem item;
        boolean isClosed;
        int highestBidder;

        LocalAuctionState(int ownerID, AuctionItem item) {
            this.ownerID = ownerID;
            this.item = item;
            this.isClosed = false;
            this.highestBidder = -1;
        }
    }

    private final Map<Integer, String> users = new HashMap<>();
    private final Map<Integer, LocalAuctionState> items = new HashMap<>();
    private int nextUserId = 0;
    private int nextItemId = 0;

    // ----- replica ID and name (keep these) ----
    private final int id;
    private final String myName;   
    public ReplicaImpl(int id, String rmiName) throws RemoteException { 
        this.id = id; 
        this.myName = rmiName; 
    }

    // ================= Auction (read-only calls are executed locally) =================
    @Override 
    public synchronized AuctionItem getSpec(int itemID) { 
        LocalAuctionState st = items.get(itemID);
        return (st == null) ? null : st.item;
    }

    @Override 
    public synchronized AuctionItem[] listItems() { 
        List<AuctionItem> active = new ArrayList<>();
        for (LocalAuctionState st : items.values()) {
            if (!st.isClosed) {
                active.add(st.item);
            }
        }
        return active.toArray(new AuctionItem[0]); 
    }

    // NOTE: this is now a local function that may be used to update local state 
    private synchronized int newAuction(int userID, AuctionSaleItem item) {
        // If userID not registered, return -1
        if (!users.containsKey(userID)) {
            return -1;
        }

        int itemID = nextItemId++;
        // Adjust field access if AuctionSaleItem uses getters instead of public fields
        AuctionItem ai = new AuctionItem(itemID, item.name, item.description, item.reservePrice);
        LocalAuctionState st = new LocalAuctionState(userID, ai);
        items.put(itemID, st);
        return itemID;
    }

    // NOTE: this is now a local function that may be used to update local state 
    private synchronized AuctionResult closeAuction(int userID, int itemID){
        LocalAuctionState st = items.get(itemID);
        if (st == null) {
            return null; // item missing
        }
        if (st.ownerID != userID) {
            return null; // only owner may close
        }
        if (st.isClosed) {
            return null; // already closed
        }

        st.isClosed = true;
        // If no bids, highestBidder will be -1 and highestBid 0
        return new AuctionResult(itemID, st.highestBidder, st.item.highestBid);
    }

    // NOTE: this is now a local function that may be used to update local state 
    private synchronized boolean bid(int userID, int itemID, int price){
        LocalAuctionState st = items.get(itemID);
        if (st == null) {
            return false; // item missing
        }
        if (!users.containsKey(userID)) {
            return false; // unknown user
        }
        if (st.isClosed) {
            return false; // no bids on closed auctions
        }

        int current = st.item.highestBid;
        int reserve = st.item.reservePrice;

        // Bid must be strictly higher than both highestBid and reserve
        if (price > current && price > reserve) {
            st.item.highestBid = price;
            st.highestBidder = userID;
            return true;
        }
        return false;
    }

    // NOTE: this is now a local function that may be used to update local state 
    private synchronized int register(String email) {
        int uid = nextUserId++;
        users.put(uid, email);
        return uid;
    }
    

    // ================= Replication core =================
    @Override
    public synchronized OperationResult handleClientOperation(Operation op, List<String> memberList) throws RemoteException {
        if (!isLeader) {
            //This should not happen
            return OperationResult.fail("Not leader");
        }

        // Step 1: Assign seqNo and add operation to local log (via propose on self)
        long seqNo = ++lastSeqAssigned;
        this.propose(seqNo, op); // logs locally

        int totalMembers = memberList.size();
        int needed = majority(totalMembers);
        int acks = 1; // leader (self) implicitly acks

        // Step 2: Propose to replicas in memberList (excluding self), ignore unreachable
        for (String name : memberList) {
            if (name.equals(myName)) {
                continue; // skip self, already proposed
            }
            try {
                ReplicatedAuction r = lookup(name);
                boolean ok = r.propose(seqNo, op);
                if (ok) {
                    acks++;
                }
            } catch (Exception e) {
                // ignore unreachable replicas
            }
        }

        // Step 3: Check majority
        if (acks < needed) {
            return OperationResult.fail("No majority quorum for operation");
        }

        // Step 3.1: Leader executes op locally, marks log entry as committed, updates seq state
        OperationResult result = apply(op);
        LogEntry le = log.get(seqNo);
        if (le != null) {
            le.committed = true;
        }
        if (seqNo > lastCommitted) {
            lastCommitted = seqNo;
        }
        if (seqNo > lastApplied) {
            // we just applied this operation in apply(), so reflect that
            lastApplied = seqNo;
        }

        // Step 3.2: Call commitUpTo on all replicas (including self), ignore unreachable
        for (String name : memberList) {
            try {
                ReplicatedAuction r = lookup(name);
                r.commitUpTo(seqNo);
            } catch (Exception e) {
                // ignore unreachable replicas during commit
            }
        }

        // Step 4: Return result from leader's apply()
        return result;
    }

    // Helper method to compute the required number of replicas to achieve majority given the number of the members
    private int majority(int n){ 
        return (n/2)+1; 
    }

    @Override
    public synchronized boolean propose(long seqNo, Operation op) {
        // Add the operation to the local Log
        LogEntry existing = log.get(seqNo);
        if (existing == null || !existing.committed) {
            log.put(seqNo, new LogEntry(seqNo, op)); 
        }
        return true;
    }

    @Override
    public synchronized boolean commitUpTo(long seqNo) {

        if (seqNo <= lastCommitted) {
            // nothing new to commit
            return true;
        }

        // Step 1 & 2: Ensure we have all entries [lastCommitted+1 .. seqNo]; if not, pull from leader
        boolean missing = false;
        for (long s = lastCommitted + 1; s <= seqNo; s++) {
            if (!log.containsKey(s)) {
                missing = true;
                break;
            }
        }

        if (missing) {
            try {
                String leaderName = findLeaderName();
                if (leaderName != null) {
                    ReplicatedAuction leader = lookup(leaderName);
                    List<LogEntry> entries = leader.getEntriesAfter(lastCommitted);
                    for (LogEntry le : entries) {
                        LogEntry local = log.get(le.seqNo);
                        if (local == null) {
                            log.put(le.seqNo, new LogEntry(le.seqNo, le.op));
                        }
                        // Mark them as committed; we will apply below
                        log.get(le.seqNo).committed = true;
                    }
                }
            } catch (Exception e) {
                // if we can't contact leader, we still try to commit what we have
            }
        }

        // Step 3: Execute and commit new operation(s) in order
        for (long s = lastApplied + 1; s <= seqNo; s++) {
            LogEntry le = log.get(s);
            if (le != null) {
                // If the leader marked it committed or we just did above for fetched entries
                if (!le.committed) {
                    le.committed = true;
                }
                apply(le.op);
                lastApplied = s;
            } else {
                // gap still present; stop
                break;
            }
        }

        if (seqNo > lastCommitted) {
            lastCommitted = seqNo;
        }

        return true;
    }

    // You may use this function to apply operations on the local state
    private OperationResult apply(Operation op){
        switch (op.type) {
            case REGISTER -> {
                int uid = register(op.email);  
                return OperationResult.reg(uid);
            }
            case NEW_AUCTION -> {
                int iid = newAuction(op.userId, new AuctionSaleItem(op.name, op.description, op.reservePrice));
                return (iid > 0) ? OperationResult.newA(iid) : OperationResult.fail("unknown user");
            }
            case BID -> {
                boolean ok = bid(op.userId, op.itemId, op.reservePrice);
                return OperationResult.bid(ok);
            }
            case CLOSE -> {
                AuctionResult ar = closeAuction(op.userId, op.itemId); // null if not owner
                if (ar == null) 
                    return OperationResult.fail("Auction close not permitted!");
                return OperationResult.close(ar.itemID, ar.winningUser, ar.price);
            }
            default -> { return OperationResult.fail("Unknown op"); }
        }
    }

    // ================= Sync & helpers =================

    @Override 
    public synchronized List<LogEntry> getEntriesAfter(long fromSeq) {
        List<LogEntry> out = new ArrayList<>();
        for (var e : log.tailMap(fromSeq+1).entrySet()) {
            LogEntry le = e.getValue();
            if (le.committed) 
                out.add(new LogEntry(le.seqNo, le.op)); 
        }
        return out;
    }

    private String findLeaderName() throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        frontend.FrontEndAdmin fe = (frontend.FrontEndAdmin) reg.lookup("FrontEnd");
        return fe.getCurrentSequencerName();
    }

    private ReplicatedAuction lookup(String rmiName) throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        return (ReplicatedAuction) reg.lookup(rmiName);
    }

    @Override 
    public synchronized long getLastCommittedSeqNo() { 
        return lastCommitted; 
    }

    // You may not use this method but it is here if you need it
    // Used to check if a replica is the leader
    @Override 
    public synchronized boolean isSequencer() { 
        return isLeader; 
    }

    // Set the replica as a leader or not (isLeader is true: you are leader, isLeader is false: you are not)
    @Override 
    public synchronized void setSequencer(boolean isLeader){ 
        this.isLeader = isLeader; 
        if (isLeader) {
            this.lastSeqAssigned = this.lastCommitted;
        }
    }
}
