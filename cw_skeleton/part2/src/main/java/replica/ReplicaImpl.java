package replica;

import common.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ReplicaImpl extends UnicastRemoteObject implements ReplicatedAuction {

    // ----- sequencing & log state -----
    private boolean isLeader = false;
    private long lastSeqAssigned = 0;   // leader uses this to assign seq numbers
    private long lastCommitted = 0;
    private long lastApplied = 0;
    private final TreeMap<Long, LogEntry> log = new TreeMap<>();

    // ----- state machine (auction logic) -----
    private final Map<Integer, String> users = new HashMap<>();
    private final Map<Integer, LocalAuctionState> items = new HashMap<>();
    private int nextUserId = 0;
    private int nextItemId = 0;

    // ----- replica ID and name -----
    private final int id;
    private final String myName;

    public ReplicaImpl(int id, String rmiName) throws RemoteException {
        super();
        this.id = id;
        this.myName = rmiName;
    }

    // Local state structure for each auction
    private static class LocalAuctionState {
        final int ownerId;
        final AuctionItem item;
        boolean isClosed = false;
        int highestBidder = -1;

        LocalAuctionState(int ownerId, AuctionItem item) {
            this.ownerId = ownerId;
            this.item = item;
        }
    }

    // ================= Auction (read-only) =================

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

    // ================= Local state machine helpers =================

    private synchronized int register(String email) {
        int uid = nextUserId++;
        users.put(uid, email);
        return uid;
    }

    private synchronized int newAuction(int userID, AuctionSaleItem sale) {
        if (!users.containsKey(userID)) {
            return -1;
        }
        int itemId = nextItemId++;
        AuctionItem item = new AuctionItem(itemId, sale.name, sale.description, sale.reservePrice);
        LocalAuctionState st = new LocalAuctionState(userID, item);
        items.put(itemId, st);
        return itemId;
    }

    private synchronized boolean bid(int userID, int itemID, int price) {
        LocalAuctionState st = items.get(itemID);
        if (st == null) return false;
        if (!users.containsKey(userID)) return false;
        if (st.isClosed) return false;

        int current = st.item.highestBid;
        int reserve = st.item.reservePrice;

        // Spec semantics: price > currentHighest AND price >= reservePrice
        if (price > current && price >= reserve) {
            st.item.highestBid = price;
            st.highestBidder = userID;
            return true;
        }
        return false;
    }

    private synchronized AuctionResult closeAuction(int userID, int itemID) {
        LocalAuctionState st = items.get(itemID);
        if (st == null) return null;
        if (st.ownerId != userID) return null;

        // Idempotent close: if already closed we just return the same outcome
        if (!st.isClosed) {
            st.isClosed = true;
        }
        return new AuctionResult(itemID, st.highestBidder, st.item.highestBid);
    }

    // Apply a log operation to local state
    private synchronized OperationResult apply(Operation op) {
        switch (op.type) {
            case REGISTER -> {
                int uid = register(op.email);
                return OperationResult.reg(uid);
            }
            case NEW_AUCTION -> {
                int iid = newAuction(op.userId,
                        new AuctionSaleItem(op.name, op.description, op.reservePrice));
                // -1 means error; 0,1,2,... are valid item IDs
                return (iid >= 0)
                        ? OperationResult.newA(iid)
                        : OperationResult.fail("unknown user");
            }
            case BID -> {
                boolean ok = bid(op.userId, op.itemId, op.reservePrice);
                return OperationResult.bid(ok);
            }
            case CLOSE -> {
                AuctionResult ar = closeAuction(op.userId, op.itemId);
                if (ar == null)
                    return OperationResult.fail("Auction close not permitted!");
                return OperationResult.close(ar.itemID, ar.winningUser, ar.price);
            }
            default -> {
                return OperationResult.fail("Unknown op");
            }
        }
    }

    // ================= Replication core =================

    private int majority(int n) {
        return (n / 2) + 1;
    }

    private ReplicatedAuction lookup(String rmiName) throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        return (ReplicatedAuction) reg.lookup(rmiName);
    }

    private String findLeaderName() throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        frontend.FrontEndAdmin fe = (frontend.FrontEndAdmin) reg.lookup("FrontEnd");
        return fe.getCurrentSequencerName();
    }

    @Override
    public synchronized OperationResult handleClientOperation(Operation op, java.util.List<String> memberList)
            throws RemoteException {

        if (!isLeader) {
            return OperationResult.fail("Not leader");
        }

        // Step 1: append to local log
        long seqNo = ++lastSeqAssigned;
        log.put(seqNo, new LogEntry(seqNo, op));

        // Step 2: propose to other replicas
        int total = memberList.size();
        int need = majority(total);
        int acks = 1; // self

        for (String name : memberList) {
            if (name.equals(myName)) continue; // don't RMI-call ourselves

            try {
                ReplicatedAuction r = lookup(name);
                if (r.propose(seqNo, op)) {
                    acks++;
                }
            } catch (Exception e) {
                // ignore unreachable replicas during propose
            }
        }

        // Step 3: check majority
        if (acks < need) {
            log.remove(seqNo);
            return OperationResult.fail("No majority quorum for seq " + seqNo);
        }

        // Step 3.1: commit + apply locally
        lastCommitted = Math.max(lastCommitted, seqNo);
        OperationResult result = apply(op);   // mutate state
        lastApplied = Math.max(lastApplied, seqNo);

        LogEntry le = log.get(seqNo);
        if (le != null) {
            le.committed = true;
        }

        // Step 3.2: tell followers to commit up to seqNo (skip self to avoid deadlock)
        for (String name : memberList) {
            if (name.equals(myName)) continue;
            try {
                ReplicatedAuction r = lookup(name);
                r.commitUpTo(seqNo);
            } catch (Exception e) {
                // ignore unreachable followers
            }
        }

        return result;
    }

    @Override
    public synchronized boolean propose(long seqNo, Operation op) {
        LogEntry existing = log.get(seqNo);
        if (existing == null || !existing.committed) {
            log.put(seqNo, new LogEntry(seqNo, op));
        }
        return true;
    }

    @Override
    public synchronized boolean commitUpTo(long seqNo) {

        // Commit and apply all entries up to seqNo, fetching missing ones from leader if needed
        while (lastCommitted < seqNo) {
            long next = lastCommitted + 1;
            LogEntry e = log.get(next);

            // Step 2: if missing entries, pull from leader
            if (e == null) {
                try {
                    String leaderName = findLeaderName();
                    if (leaderName == null) break;
                    ReplicatedAuction leader = lookup(leaderName);
                    java.util.List<LogEntry> missing = leader.getEntriesAfter(next - 1);
                    for (LogEntry le : missing) {
                        if (!log.containsKey(le.seqNo)) {
                            log.put(le.seqNo, new LogEntry(le.seqNo, le.op));
                        }
                    }
                    e = log.get(next);
                    if (e == null) break; // still missing, give up
                } catch (Exception ex) {
                    break;
                }
            }

            // Step 3: execute and commit
            if (next > lastApplied) {
                apply(e.op);
                lastApplied = next;
            }
            e.committed = true;
            lastCommitted = next;
        }

        return true;
    }

    @Override
    public synchronized java.util.List<LogEntry> getEntriesAfter(long fromSeq) {
        java.util.List<LogEntry> out = new ArrayList<>();
        for (var entry : log.tailMap(fromSeq + 1).entrySet()) {
            LogEntry le = entry.getValue();
            if (le.committed) {
                out.add(new LogEntry(le.seqNo, le.op));
            }
        }
        return out;
    }

    @Override
    public synchronized long getLastCommittedSeqNo() {
        return lastCommitted;
    }

    @Override
    public boolean isSequencer() {
        return isLeader;
    }

    @Override
    public void setSequencer(boolean isLeader) {
        this.isLeader = isLeader;
        if (isLeader) {
            this.lastSeqAssigned = this.lastCommitted;
        }
    }
}
