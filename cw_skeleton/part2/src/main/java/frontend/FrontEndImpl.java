package frontend;

import io.grpc.stub.StreamObserver;

import common.*;
import replica.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class FrontEndImpl extends AuctionServiceGrpc.AuctionServiceImplBase implements FrontEndAdmin {

    // ============= Front-end state (replica membership + sequencer) =============
    // Name of the current sequencer (RMI binding name, e.g. "replica1")
    private volatile String sequencerName = null;

    // List of all known replicas (their RMI names), maintained by FrontEndAdmin.registerReplica
    private final List<String> members = new ArrayList<>();

    public FrontEndImpl() throws RemoteException {
        super();
    }

    // ======================= FrontEndAdmin (RMI) =======================

    @Override
    public synchronized String getCurrentSequencerName() throws RemoteException {
        return sequencerName;
    }

    @Override
    public synchronized void registerReplica(int id, String rmiName) throws RemoteException {
        // Avoid duplicates
        if (!members.contains(rmiName)) {
            members.add(rmiName);
            System.out.println("FrontEnd: registered replica " + rmiName);
        }

        // If no sequencer yet, make this replica the leader
        if (sequencerName == null) {
            try {
                ReplicatedAuction ra = lookup(rmiName);
                ra.setSequencer(true);
                sequencerName = rmiName;
                System.out.println("FrontEnd: " + rmiName + " set as initial sequencer");
            } catch (Exception e) {
                System.err.println("FrontEnd: failed to set " + rmiName + " as sequencer: " + e);
            }
        }
    }

    // ======================= Helper: leader election =======================

    // Elects a new leader among current members based on highest lastCommitted sequence.
    private synchronized void electNewLeader() throws Exception {
        long bestSeq = -1;
        String bestName = null;

        System.out.println("FrontEnd: starting leader election among " + members.size() + " replicas");

        for (String name : members) {
            try {
                ReplicatedAuction r = lookup(name);
                long seq = r.getLastCommittedSeqNo();
                if (seq > bestSeq) {
                    bestSeq = seq;
                    bestName = name;
                }
            } catch (Exception e) {
                // ignore unreachable replica during election
                System.err.println("FrontEnd: replica " + name + " unreachable during election: " + e);
            }
        }

        if (bestName == null) {
            throw new IllegalStateException("No reachable replicas to elect as leader");
        }

        // Promote the chosen replica
        ReplicatedAuction newLeader = lookup(bestName);
        newLeader.setSequencer(true);

        // Optionally demote old leader (best-effort)
        if (sequencerName != null && !sequencerName.equals(bestName)) {
            try {
                ReplicatedAuction oldLeader = lookup(sequencerName);
                oldLeader.setSequencer(false);
            } catch (Exception e) {
                // ignore failures demoting old leader
            }
        }

        sequencerName = bestName;
        System.out.println("FrontEnd: elected new sequencer: " + sequencerName + " (lastCommitted=" + bestSeq + ")");
    }

    // Helper to get a snapshot of member list (to avoid concurrent modification issues)
    private synchronized List<String> memberSnapshot() {
        return new ArrayList<>(members);
    }

    // Helper: call handleClientOperation on the current leader with one retry on failure
    private OperationResult invokeOnLeader(Operation op) throws Exception {
        // First attempt: current leader
        try {
            ReplicatedAuction leader = lookupLeader();
            return leader.handleClientOperation(op, memberSnapshot());
        } catch (Exception e) {
            System.err.println("FrontEnd: leader call failed (" + e + "), trying election...");
            // Try to elect a new leader and retry once
            electNewLeader();
            ReplicatedAuction newLeader = lookupLeader();
            return newLeader.handleClientOperation(op, memberSnapshot());
        }
    }

    // ======================= gRPC: Register =======================

    @Override
    public void register(RegisterRequest req, StreamObserver<RegisterReply> resp) {
        try {
            Operation op = Operation.register(req.getEmail());
            OperationResult or = invokeOnLeader(op);

            int uid = -1;
            if (or.ok && or.userId != null) {
                uid = or.userId;
            }

            RegisterReply reply = RegisterReply.newBuilder()
                    .setUserId(uid)
                    .build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= gRPC: NewAuction =======================

    @Override
    public void newAuction(NewAuctionRequest req, StreamObserver<NewAuctionReply> resp) {
        try {
            Operation op = Operation.newAuction(
                    req.getUserId(),
                    req.getName(),
                    req.getDescription(),
                    req.getReservePrice()
            );

            OperationResult or = invokeOnLeader(op);

            int itemId = -1;
            if (or.ok && or.itemId != null) {
                itemId = or.itemId;
            }

            NewAuctionReply reply = NewAuctionReply.newBuilder()
                    .setItemId(itemId)
                    .build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= gRPC: Bid =======================

    @Override
    public void bid(BidRequest req, StreamObserver<BidReply> resp) {
        try {
            Operation op = Operation.bid(req.getUserId(), req.getItemId(), req.getPrice());
            OperationResult or = invokeOnLeader(op);

            boolean success = false;
            if (or.ok && or.bidOk != null) {
                success = or.bidOk;
            }

            BidReply reply = BidReply.newBuilder()
                    .setSuccess(success)
                    .build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= gRPC: ListItems (read-only) =======================

    @Override
    public void listItems(Empty req, StreamObserver<ListReply> resp) {
        try {
            AuctionItem[] items;
            try {
                ReplicatedAuction leader = lookupLeader();
                items = leader.listItems();
            } catch (Exception e) {
                // leader might be dead → elect new one, then retry
                electNewLeader();
                ReplicatedAuction leader = lookupLeader();
                items = leader.listItems();
            }

            ListReply.Builder builder = ListReply.newBuilder();
            if (items != null) {
                for (AuctionItem ai : items) {
                    Item itemMsg = Item.newBuilder()
                            .setItemId(ai.itemID)
                            .setName(ai.name)
                            .setDescription(ai.description)
                            .setReservePrice(ai.reservePrice)
                            .setHighestBid(ai.highestBid)
                            .build();
                    builder.addItems(itemMsg);
                }
            }

            resp.onNext(builder.build());
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= gRPC: GetSpec (read-only) =======================

    @Override
    public void getSpec(GetSpecRequest req, StreamObserver<Item> resp) {
        try {
            AuctionItem ai;
            try {
                ReplicatedAuction leader = lookupLeader();
                ai = leader.getSpec(req.getItemId());
            } catch (Exception e) {
                electNewLeader();
                ReplicatedAuction leader = lookupLeader();
                ai = leader.getSpec(req.getItemId());
            }

            Item reply;
            if (ai == null) {
                reply = Item.newBuilder().build(); // empty
            } else {
                reply = Item.newBuilder()
                        .setItemId(ai.itemID)
                        .setName(ai.name)
                        .setDescription(ai.description)
                        .setReservePrice(ai.reservePrice)
                        .setHighestBid(ai.highestBid)
                        .build();
            }

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= gRPC: CloseAuction =======================

    @Override
    public void closeAuction(CloseRequest req, StreamObserver<AuctionResult> resp) {
        try {
            Operation op = Operation.close(req.getUserId(), req.getItemId());
            OperationResult or = invokeOnLeader(op);

            int itemId = 0;
            int winner = 0;
            int price = 0;

            if (or.ok && or.closeItem != null && or.closeWinner != null && or.closePrice != null) {
                itemId = or.closeItem;
                winner = or.closeWinner;
                price = or.closePrice;
            }

            AuctionResult reply = AuctionResult.newBuilder()
                    .setItemId(itemId)
                    .setWinningUser(winner)
                    .setPrice(price)
                    .build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    // ======================= RMI helper methods =======================

    // Looks up and returns a remote reference to the specified replica in the local RMI registry.
    private ReplicatedAuction lookup(String rmiName) throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        return (ReplicatedAuction) reg.lookup(rmiName);
    }

    // Looks up and returns a remote reference to the current sequencer
    private ReplicatedAuction lookupLeader() throws Exception {
        String name = sequencerName;
        if (name == null) {
            throw new IllegalStateException("No sequencer set");
        }
        return lookup(name);
    }
}
