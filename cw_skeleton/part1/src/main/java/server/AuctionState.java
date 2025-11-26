package server;

public class AuctionState {
    final int ownerID;
    final AuctionItem item;
    private boolean isClosed;
    private int highestBidder;

    public AuctionState(int ownerID, AuctionItem item) {
        this.ownerID = ownerID;
        this.item = item;
        this.isClosed = false;
        this.highestBidder = -1;
    }

    public void setHighestBidAndBidder(int bidderID, int bid){
        if(bid < 0){
            return;
        }
        this.item.highestBid = bid;
        this.highestBidder = bidderID;
    }

    public int getHighestBidder(){
        return highestBidder;
    }

    public int getHighestBid(){
        return item.highestBid;
    }

    public int getReservePrice(){
        return item.reservePrice;
    }

    public boolean getIsClosed(){
        return isClosed;
    }

    public void closeAuction(){
        isClosed = true;
    }
}
