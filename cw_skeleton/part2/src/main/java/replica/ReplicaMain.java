package replica;

import common.*;
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
        
        //TODO (suggested high-level steps):
        // Step 1: Ask the front-end to find out who the current sequencer (leader) is. If the answer is null (no leader), then register with the front-end (registerReplica)
        // Step 2: Retrieve any missing committed log entries from the leader 
        // Step 3: Locally execute any new committed (previously missing) operations that were added to the log in the previous step
        // Step 4: Now that the replica is ready to serve requests, register with the front-end (front-end maintains replica membership) 

        //NOTE: you may skip steps 1--3 and only do step 4 in which case criterion 2.3 will not be satisfied

    }
}
