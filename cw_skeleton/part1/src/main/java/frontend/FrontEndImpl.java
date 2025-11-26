package frontend;

import io.grpc.stub.StreamObserver;
import server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FrontEndImpl extends AuctionServiceGrpc.AuctionServiceImplBase {
    private final Auction auction;

    public FrontEndImpl() throws Exception {
        Registry reg = LocateRegistry.getRegistry();
        this.auction = (Auction) reg.lookup("AuctionServer");
    }

    @Override
    public void register(RegisterRequest req, StreamObserver<RegisterReply> resp) {
        try {
            int id = auction.register(req.getEmail());
            resp.onNext(RegisterReply.newBuilder().setUserId(id).build());
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    @Override
    public void newAuction(NewAuctionRequest req, StreamObserver<NewAuctionReply> resp) {
        // Construct an AuctionSaleItem from the gRPC request fields.
        // Forward newAuction(userId, item) to the RMI Auction server.
        // Build and return a NewAuctionReply with the created itemId.
        try {
            AuctionSaleItem newItem = new AuctionSaleItem(req.getName(),req.getDescription(),req.getReservePrice());

            int itemID = auction.newAuction(req.getUserId(), newItem);

            NewAuctionReply reply = NewAuctionReply.newBuilder()
                    .setItemId(itemID)
                    .build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    @Override
    public void bid(BidRequest req, StreamObserver<BidReply> resp) {
        // Forward bid(userId, itemId, price) to the RMI Auction server.
        // Build and return a BidReply with success=true/false.
        try {
            boolean ok = auction.bid(req.getUserId(), req.getItemId(), req.getPrice());

            BidReply reply = BidReply.newBuilder().setSuccess(ok).build();

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    @Override
    public void listItems(Empty req, StreamObserver<ListReply> resp) {
        // Call auction.listItems() on the RMI server.
        // Map each AuctionItem to the gRPC Item message.
        // Build and return a ListReply containing all items.
        try {
            AuctionItem[] items = auction.listItems();

            ListReply.Builder replyBuilder = ListReply.newBuilder();

            if (items != null) {
                for (AuctionItem ai : items) {
                    Item itemMsg = Item.newBuilder().setItemId(ai.itemID).setName(ai.name).setDescription(ai.description).setReservePrice(ai.reservePrice).setHighestBid(ai.highestBid).build();
                    replyBuilder.addItems(itemMsg);
                }
            }

            resp.onNext(replyBuilder.build());
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    @Override
    public void getSpec(GetSpecRequest req, StreamObserver<Item> resp) {
        // Call auction.getSpec(itemId) on the RMI server.
        // If null, return an empty Item message.
        // Otherwise, map fields to a gRPC Item and return it.
        try {
            AuctionItem ai = auction.getSpec(req.getItemId());

            Item reply;
            if (ai == null) {
                // Empty/default item
                reply = Item.newBuilder().build();
            } else {
                reply = Item.newBuilder().setItemId(ai.itemID).setName(ai.name).setDescription(ai.description).setReservePrice(ai.reservePrice).setHighestBid(ai.highestBid).build();
            }

            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }

    @Override
    public void closeAuction(CloseRequest req, StreamObserver<AuctionResult> resp) {
        // Forward closeAuction(userId, itemId) to the RMI Auction server.
        // If the result is null (e.g., wrong owner or already closed), return
        // an AuctionResult with zeroed fields.
        // Otherwise, map the AuctionResult fields and return them.
        try {
            // server.AuctionResult is the RMI-side class (from package server)
            server.AuctionResult res = auction.closeAuction(req.getUserId(), req.getItemId());

            AuctionResult reply;
            if (res == null) {
                // Zeroed fields if invalid close
                reply = AuctionResult.newBuilder().setItemId(0).setWinningUser(0).setPrice(0).build();
            } else {
                reply = AuctionResult.newBuilder().setItemId(res.itemID).setWinningUser(res.winningUser).setPrice(res.price).build();
            }
            resp.onNext(reply);
            resp.onCompleted();
        } catch (Exception e) {
            resp.onError(e);
        }
    }
}
