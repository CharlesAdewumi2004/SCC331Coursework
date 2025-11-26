package replica;

import common.*;
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

        // 1) Create and bind the replica in the local RMI registry
        ReplicaImpl r = new ReplicaImpl(id, name);
        Registry reg = LocateRegistry.getRegistry();
        reg.rebind(name, r);
        System.out.println("Replica " + id + " bound as " + name);

        try {
            // 2) Contact the front-end admin
            FrontEndAdmin fe = (FrontEndAdmin) reg.lookup("FrontEnd");

            // 3) Find current sequencer (if any)
            String leaderName = fe.getCurrentSequencerName();

            if (leaderName != null) {
                // There is an existing leader: pull committed log entries
                System.out.println("Existing leader is " + leaderName + ", syncing state...");

                ReplicatedAuction leader = (ReplicatedAuction) reg.lookup(leaderName);

                // Get all committed entries from seqNo > 0
                long lastCommittedOnLeader = leader.getLastCommittedSeqNo();
                if (lastCommittedOnLeader > 0) {
                    // Pull entries and apply them via commitUpTo
                    // First, copy the committed log entries into our local log
                    for (LogEntry le : leader.getEntriesAfter(0)) {
                        r.propose(le.seqNo, le.op);
                        // we mark committed later in commitUpTo
                    }
                    // Now commit and apply all operations up to leader's last committed seqNo
                    r.commitUpTo(lastCommittedOnLeader);
                    System.out.println("Replica " + id + " synced up to seq " + lastCommittedOnLeader);
                }
            } else {
                System.out.println("No existing leader reported by FrontEnd; this may be the first replica.");
            }

            // 4) Now that we are up-to-date, register this replica with the front-end
            fe.registerReplica(id, name);
            System.out.println("Replica " + id + " registered with FrontEnd");

        } catch (Exception e) {
            System.err.println("Error during replica initialisation: " + e);
            e.printStackTrace();
        }

        // Keep the replica alive
        System.out.println("Replica " + id + " ready.");
    }
}
