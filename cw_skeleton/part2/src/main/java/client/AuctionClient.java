package client;

import frontend.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AuctionClient {
    public static void main(String[] args) {
        System.out.println("AuctionClient starting...");

        ManagedChannel ch = ManagedChannelBuilder.forAddress("localhost", 50055)
                .usePlaintext()
                .build();
        var stub = AuctionServiceGrpc.newBlockingStub(ch);

        try {
            System.out.println("Registering users...");
            int alice = stub.register(RegisterRequest.newBuilder()
                            .setEmail("alice@lancaster.ac.uk").build())
                    .getUserId();
            int bob = stub.register(RegisterRequest.newBuilder()
                            .setEmail("bob@lancaster.ac.uk").build())
                    .getUserId();
            int carol = stub.register(RegisterRequest.newBuilder()
                            .setEmail("carol@lancaster.ac.uk").build())
                    .getUserId();

            System.out.printf("Users -> alice=%d bob=%d carol=%d%n", alice, bob, carol);

            // Start auctions
            NewAuctionReply ps5Reply = stub.newAuction(
                    NewAuctionRequest.newBuilder()
                            .setUserId(alice)
                            .setName("PS5")
                            .setDescription("PlayStation 5 console")
                            .setReservePrice(300)
                            .build()
            );
            int ps5Id = ps5Reply.getItemId();
            System.out.printf("Alice starts PS5 auction -> itemId=%d%n", ps5Id);

            NewAuctionReply laptopReply = stub.newAuction(
                    NewAuctionRequest.newBuilder()
                            .setUserId(bob)
                            .setName("Gaming Laptop")
                            .setDescription("RTX 4070, 16GB RAM")
                            .setReservePrice(800)
                            .build()
            );
            int laptopId = laptopReply.getItemId();
            System.out.printf("Bob starts Laptop auction -> itemId=%d%n", laptopId);

            // Bids
            boolean b1 = stub.bid(BidRequest.newBuilder()
                            .setUserId(bob).setItemId(ps5Id).setPrice(200).build())
                    .getSuccess();
            System.out.printf("Bob bids 200 on PS5 (expect false): %s%n", b1);

            boolean b2 = stub.bid(BidRequest.newBuilder()
                            .setUserId(carol).setItemId(ps5Id).setPrice(350).build())
                    .getSuccess();
            System.out.printf("Carol bids 350 on PS5 (expect true): %s%n", b2);

            boolean b3 = stub.bid(BidRequest.newBuilder()
                            .setUserId(alice).setItemId(ps5Id).setPrice(400).build())
                    .getSuccess();
            System.out.printf("Alice bids 400 on PS5 (expect true): %s%n", b3);

            boolean b4 = stub.bid(BidRequest.newBuilder()
                            .setUserId(carol).setItemId(laptopId).setPrice(900).build())
                    .getSuccess();
            System.out.printf("Carol bids 900 on Laptop (expect true): %s%n", b4);

            // List items
            System.out.println("\nCurrent items:");
            ListReply list = stub.listItems(Empty.newBuilder().build());
            for (Item it : list.getItemsList()) {
                System.out.printf("Item %d: %s | reserve=%d | highest=%d%n",
                        it.getItemId(), it.getName(), it.getReservePrice(), it.getHighestBid());
            }

            // Get spec
            System.out.println("\nSpec for PS5:");
            Item ps5Spec = stub.getSpec(GetSpecRequest.newBuilder().setItemId(ps5Id).build());
            System.out.printf("PS5 -> id=%d, name=%s, desc=%s, reserve=%d, highest=%d%n",
                    ps5Spec.getItemId(),
                    ps5Spec.getName(),
                    ps5Spec.getDescription(),
                    ps5Spec.getReservePrice(),
                    ps5Spec.getHighestBid());

            // Close auction
            System.out.println("\nClosing PS5 auction (by owner Alice):");
            frontend.AuctionResult closePs5 = stub.closeAuction(
                    CloseRequest.newBuilder().setUserId(alice).setItemId(ps5Id).build());
            System.out.printf("Close result -> item=%d, winner=%d, price=%d%n",
                    closePs5.getItemId(), closePs5.getWinningUser(), closePs5.getPrice());

        } finally {
            System.out.println("Shutting down client...");
            ch.shutdown();
        }
    }
}
