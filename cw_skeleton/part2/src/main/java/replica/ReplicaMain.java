package replica;

import frontend.FrontEndAdmin;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReplicaMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: ReplicaMain <id>");
            System.exit(1);
        }
        int id = Integer.parseInt(args[0]);
        String name = "replica" + id;

        // Register the replica with rmiregistry
        ReplicatedAuction r = new ReplicaImpl(id, name);
        Registry reg = LocateRegistry.getRegistry();
        reg.rebind(name, r);
        System.out.println("Replica " + id + " bound as " + name);

        // ----- Optional startup sync (criterion 2.3) -----
        try {
            Registry rmiReg = LocateRegistry.getRegistry();
            FrontEndAdmin fe = (FrontEndAdmin) rmiReg.lookup("FrontEnd");

            String leaderName = fe.getCurrentSequencerName();

            if (leaderName != null && !leaderName.equals(name)) {
                System.out.println("Existing leader is " + leaderName + ", syncing state...");
                ReplicatedAuction leader = (ReplicatedAuction) rmiReg.lookup(leaderName);
                long lastCommitted = leader.getLastCommittedSeqNo();
                if (lastCommitted > 0) {
                    r.commitUpTo(lastCommitted);
                }
            } else if (leaderName == null) {
                System.out.println("No existing leader reported by FrontEnd; this may be the first replica.");
            }

            // Now register this replica as a member
            fe.registerReplica(id, name);
            System.out.println("Replica " + id + " registered with FrontEnd");

        } catch (Exception e) {
            System.err.println("Error during replica initialisation: " + e);
        }

        System.out.println("Replica " + id + " ready.");

        // Keep JVM alive
        new java.util.concurrent.CountDownLatch(1).await();
    }
}
