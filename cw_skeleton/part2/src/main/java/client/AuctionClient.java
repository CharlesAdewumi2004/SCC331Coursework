package client;

import frontend.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// A sample client which performs basic checks

public class AuctionClient {
    public static void main(String[] args) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress("localhost", 50055)
                .usePlaintext().build();
        var stub = AuctionServiceGrpc.newBlockingStub(ch);

        // Register three users
        int alice = stub.register(RegisterRequest.newBuilder().setEmail("alice@lancaster.ac.uk").build()).getUserId();
        int bob   = stub.register(RegisterRequest.newBuilder().setEmail("bob@lancaster.ac.uk").build()).getUserId();
        int carol = stub.register(RegisterRequest.newBuilder().setEmail("carol@lancaster.ac.uk").build()).getUserId();
        System.out.printf("Users -> alice=%d bob=%d carol=%d%n", alice, bob, carol);

        // TODO:  make sure to test the functionality before submitting!
 
        // 1. Start a few auctions using one or more of the registered users.
        //    - Construct and send NewAuctionRequest messages.
        //    - Print returned item IDs.
        //
        // 2. Have multiple users place bids on these items.
        //    - Use BidRequest messages.
        //    - Print whether each bid was accepted or rejected.
        //
        // 3. Test listing and inspecting items.
        //    - Call listItems() to verify current highest bids and reserve prices.
        //    - Optionally call getSpec() for a specific item.
        //
        // 4. Close an auction.
        //    - Ensure only the creator can close it.
        //    - Print the returned AuctionResult.
        //
        // 5. Try edge cases:
        //    - Bidding on a non-existent item.
        //    - Bidding below reserve price.
        //    - Closing an auction twice.
        //    - Closing an auction by a non-owner.
        //
        // 6. Print a summary of expected vs. actual outcomes for basic validation.

        ch.shutdown();
    }
}
