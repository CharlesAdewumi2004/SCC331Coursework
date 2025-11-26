package frontend;

import io.grpc.stub.StreamObserver;
import common.*;
import replica.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class FrontEndImpl extends AuctionServiceGrpc.AuctionServiceImplBase implements FrontEndAdmin {


    //TODO:
    // Add state variables
    private volatile String sequencerName = null;

    // === FrontEndAdmin ===
    @Override 
    public String getCurrentSequencerName() throws RemoteException 
    { 
        return sequencerName; 
    }

    @Override 
    public void registerReplica(int id, String rmiName) throws RemoteException {
        //TODO:
        // Add the new member to the list of members
        // If no sequencer (leader) assigned, make the first one to register the sequencer by calling setSequencer(true) on the replica

        System.out.println("Registered replica " + rmiName + "; leader=" + sequencerName);
    }

    @Override
    public void getSpec(GetSpecRequest req, StreamObserver<Item> resp) {
        //TODO:
        // Call getSpec directly on the current sequencer
        // Handle any errors (you may need to elect a new leader if the current one has crashed) 
        // I suggest you implement leader election in the skeleton method provided below (electNewLeader)
        // NOTE: if you elect a new leader, you have to call getSpec on the new leader
    }


    // ===== gRPC: READS (direct to leader's Auction API) =====
    @Override
    public void listItems(Empty req, StreamObserver<ListReply> resp) {
        //TODO:
        //Call listItems on the current sequencer
        // Handle any errors (you may need to elect a new leader if the current one has crashed) 
        // I suggest you implement leader election in the skeleton method provided below (electNewLeader)
        // NOTE: if you elect a new leader, you have to call listItems on the new leader
    }

    @Override
    public void register(RegisterRequest req, StreamObserver<RegisterReply> resp) {

        //TODO: Suggested (high-level) steps
        // Step 1: Lookup the current sequencer (leader)
        // Step 2: Create an Operation object (you can do: op = Operation.register(req.getEmail()))
        // Step 3: Call the handleClientOperation on the leader, passing the operation and current list of members (including leader)
        // Step 4: Collect OperationResult returned by the call and return it back to the client using gRPC
        // NOTE: you must handle leader failure (elect new one and repeat step 3 on the new leader)
    }

    // ===== gRPC: State-mutating calls =====
    @Override
    public void newAuction(NewAuctionRequest req, StreamObserver<NewAuctionReply> resp) {
        //TODO: Suggested (high-level) steps
        // Step 1: Lookup the current sequencer (leader)
        // Step 2: Create an Operation object (you can do: op = Operation.newAuction(...))
        // Step 3: Call the handleClientOperation on the leader, passing the operation and current list of members (including leader)
        // Step 4: Collect OperationResult returned by the call and return it back to the client using gRPC
        // NOTE: you must handle leader failure (elect new one and repeat step 3 on the new leader)
    }

    @Override
    public void bid(BidRequest req, StreamObserver<BidReply> resp) {
        //TODO: Suggested (high-level) steps
        // Step 1: Lookup the current sequencer (leader)
        // Step 2: Create an Operation object (you can do: op = Operation.bid(req.getUserId(), ...))
        // Step 3: Call the handleClientOperation on the leader, passing the operation and current list of members (including leader)
        // Step 4: Collect OperationResult returned by the call and return it back to the client using gRPC
        // NOTE: you must handle leader failure (elect new one and repeat step 3 on the new leader)
    }

    @Override
    public void closeAuction(CloseRequest req, StreamObserver<AuctionResult> resp) {
        //TODO: Suggested (high-level) steps
        // Step 1: Lookup the current sequencer (leader)
        // Step 2: Create an Operation object (you can do: op = Operation.close(req.getUserId(), ...))
        // Step 3: Call the handleClientOperation on the leader, passing the operation and current list of members (including leader)
        // Step 4: Collect OperationResult returned by the call and return it back to the client using gRPC
        // NOTE: you must handle leader failure (elect new one and repeat step 3 on the new leader)

    }

    // I suggest implementing leader election in this method and calling from other methods when needed
    private synchronized void electNewLeader() {
        //TODO:
        // probe all members, pick the replica that reports the highest lastCommitted (if tie → pick any)
        // Call setSequencer(true) on the selected replica (optionally call setSequencer(false) on the others)

        System.out.println("Elected new sequencer: " + sequencerName);
    }
    
    // ===== Helpers that may be useful =====
    
    // Looks up and returns a remote reference to the specified replica in the local RMI registry.
    private ReplicatedAuction lookup(String rmiName) throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        return (ReplicatedAuction) reg.lookup(rmiName);
    }

    // Looks up and returns a remote reference to the current sequencer
    private ReplicatedAuction lookupLeader() throws Exception {
        if (sequencerName == null) throw new IllegalStateException("No sequencer set");
        return lookup(sequencerName);
    }

}
