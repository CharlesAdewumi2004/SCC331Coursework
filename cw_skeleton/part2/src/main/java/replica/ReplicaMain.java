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
        ReplicatedAuction r = new ReplicaImpl(id, name);
        Registry reg = LocateRegistry.getRegistry();
        reg.rebind(name, r);
        System.out.println("Replica " + id + " bound as " + name);

        try {
            // 2) Get FrontEndAdmin and register this replica
            FrontEndAdmin fe = (FrontEndAdmin) reg.lookup("FrontEnd");
            // We are NOT doing any sync-from-leader here (skipping steps 1–3 from the spec).
            fe.registerReplica(id, name);
            System.out.println("Replica " + id + " registered with FrontEnd");
        } catch (Exception e) {
            System.err.println("Error during replica initialisation: " + e);
            e.printStackTrace();
        }

        System.out.println("Replica " + id + " ready.");

        // 3) Keep JVM alive so this replica stays reachable via RMI
        try {
            new java.util.concurrent.CountDownLatch(1).await();
        } catch (InterruptedException ignored) {
        }
    }
}
