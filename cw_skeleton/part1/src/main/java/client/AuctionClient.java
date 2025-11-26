package client;

import frontend.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AuctionClient {
    public static void main(String[] args) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress("localhost", 50055)
                .usePlaintext()
                .build();

        var stub = AuctionServiceGrpc.newBlockingStub(ch);

        // 0. Register three users
        int alice = stub.register(
                RegisterRequest.newBuilder().setEmail("alice@lancaster.ac.uk").build()
        ).getUserId();
        int bob = stub.register(
                RegisterRequest.newBuilder().setEmail("bob@lancaster.ac.uk").build()
        ).getUserId();
        int carol = stub.register(
                RegisterRequest.newBuilder().setEmail("carol@lancaster.ac.uk").build()
        ).getUserId();

        System.out.printf("Users -> alice=%d bob=%d carol=%d%n", alice, bob, carol);

        // 1. Start a few auctions
        NewAuctionReply a1 = stub.newAuction(
                NewAuctionRequest.newBuilder()
                        .setUserId(alice)
                        .setName("PS5")
                        .setDescription("PlayStation 5 console")
                        .setReservePrice(300)
                        .build()
        );
        int ps5Id = a1.getItemId();

        NewAuctionReply a2 = stub.newAuction(
                NewAuctionRequest.newBuilder()
                        .setUserId(bob)
                        .setName("Gaming Laptop")
                        .setDescription("16GB RAM, RTX GPU")
                        .setReservePrice(800)
                        .build()
        );
        int laptopId = a2.getItemId();

        System.out.printf("Auctions -> PS5=%d, Laptop=%d%n", ps5Id, laptopId);

        // 2. Place some bids
        // Bob bids on Alice's PS5 below reserve (should fail)
        BidReply b1 = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(bob)
                        .setItemId(ps5Id)
                        .setPrice(200)  // below reserve 300
                        .build()
        );
        System.out.printf("Bob bids 200 on PS5 (expect false): %b%n", b1.getSuccess());

        // Carol bids on PS5 at exactly reserve (depending on your logic this may succeed/fail)
        BidReply b2 = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(carol)
                        .setItemId(ps5Id)
                        .setPrice(350)
                        .build()
        );
        System.out.printf("Carol bids 350 on PS5: %b%n", b2.getSuccess());

        // Alice bids on her own PS5 (allowed in our logic)
        BidReply b3 = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(alice)
                        .setItemId(ps5Id)
                        .setPrice(400)
                        .build()
        );
        System.out.printf("Alice bids 400 on PS5: %b%n", b3.getSuccess());

        // Carol bids on Bob's laptop
        BidReply b4 = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(carol)
                        .setItemId(laptopId)
                        .setPrice(900)
                        .build()
        );
        System.out.printf("Carol bids 900 on Laptop: %b%n", b4.getSuccess());

        // 3. List items and inspect
        System.out.println("\nCurrent items:");
        ListReply list = stub.listItems(Empty.newBuilder().build());
        for (Item it : list.getItemsList()) {
            System.out.printf(
                    "Item %d: %s | reserve=%d | highest=%d%n",
                    it.getItemId(), it.getName(), it.getReservePrice(), it.getHighestBid()
            );
        }

        // Get spec for PS5
        System.out.println("\nSpec for PS5:");
        Item ps5Spec = stub.getSpec(GetSpecRequest.newBuilder().setItemId(ps5Id).build());
        System.out.printf(
                "PS5 -> id=%d, name=%s, desc=%s, reserve=%d, highest=%d%n",
                ps5Spec.getItemId(),
                ps5Spec.getName(),
                ps5Spec.getDescription(),
                ps5Spec.getReservePrice(),
                ps5Spec.getHighestBid()
        );

        // 4. Close an auction
        System.out.println("\nClosing PS5 auction (by owner Alice):");
        AuctionResult close1 = stub.closeAuction(
                CloseRequest.newBuilder()
                        .setUserId(alice)
                        .setItemId(ps5Id)
                        .build()
        );
        System.out.printf(
                "Close result -> item=%d, winner=%d, price=%d%n",
                close1.getItemId(), close1.getWinningUser(), close1.getPrice()
        );

        // 5. Edge cases

        // 5a. Bidding on closed auction (should fail)
        BidReply afterCloseBid = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(bob)
                        .setItemId(ps5Id)
                        .setPrice(500)
                        .build()
        );
        System.out.printf("Bob bids 500 on CLOSED PS5 (expect false): %b%n", afterCloseBid.getSuccess());

        // 5b. Closing same auction again (your impl returns zeroed result via frontend)
        System.out.println("\nClosing PS5 again (still Alice):");
        AuctionResult closeAgain = stub.closeAuction(
                CloseRequest.newBuilder()
                        .setUserId(alice)
                        .setItemId(ps5Id)
                        .build()
        );
        System.out.printf(
                "Second close result -> item=%d, winner=%d, price=%d%n",
                closeAgain.getItemId(), closeAgain.getWinningUser(), closeAgain.getPrice()
        );

        // 5c. Closing Bob's laptop by Carol (not owner)
        System.out.println("\nClosing Laptop by non-owner Carol (should fail / zeroed):");
        AuctionResult closeWrongOwner = stub.closeAuction(
                CloseRequest.newBuilder()
                        .setUserId(carol)
                        .setItemId(laptopId)
                        .build()
        );
        System.out.printf(
                "Close by Carol -> item=%d, winner=%d, price=%d%n",
                closeWrongOwner.getItemId(), closeWrongOwner.getWinningUser(), closeWrongOwner.getPrice()
        );

        // 5d. Bidding on non-existent item
        System.out.println("\nBidding on non-existent itemId=9999:");
        BidReply nonExistentBid = stub.bid(
                BidRequest.newBuilder()
                        .setUserId(alice)
                        .setItemId(9999)
                        .setPrice(1000)
                        .build()
        );
        System.out.printf("Bid on non-existent item (expect false): %b%n", nonExistentBid.getSuccess());

        ch.shutdown();
    }
}
