package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;

public class AuctionImpl extends UnicastRemoteObject implements Auction {

    private ConcurrentHashMap<Integer, String> Users = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, AuctionState> Items = new ConcurrentHashMap<>();
    private AtomicInteger NumOfUsers = new AtomicInteger(0);
    private AtomicInteger NumOfItems = new AtomicInteger(0);

    public AuctionImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized int register(String email) {
        // TODO:
        // - Allocate a new userID
        // - Record mapping userID -> email
        // - Return the new userID
        int userID = NumOfUsers.getAndIncrement();
        Users.put(userID, email);

        return userID; // TODO: replace with allocated userID
    }

    @Override
    public synchronized int newAuction(int userID, AuctionSaleItem item) {
        // TODO:
        // - If userID not registered, return -1
        // - Create a new itemID
        // - Store AuctionItem with initial highestBid = 0
        // - Record itemOwner[itemID] = userID
        // - Return itemID

        if (!Users.containsKey(userID)) {
            return -1;
        }

        int itemID = NumOfItems.getAndIncrement();
        AuctionItem newItem = new AuctionItem(itemID, item.name, item.description, item.reservePrice);

        AuctionState auctionMetadata = new AuctionState(userID, newItem);

        Items.put(itemID, auctionMetadata);

        return itemID;
    }

    @Override
    public synchronized AuctionItem getSpec(int itemID) {
        // TODO:
        // - Return the AuctionItem for itemID, or null if not found
        return Items.containsKey(itemID) ? Items.get(itemID).item : null;
    }

    @Override
    public synchronized AuctionItem[] listItems() {
        ArrayList<AuctionItem> activeItems = new ArrayList<>();

        for (AuctionState itemMetadata : Items.values()) {
            if (!itemMetadata.getIsClosed()) {
                activeItems.add(itemMetadata.item);
            }
        }

        return activeItems.toArray(new AuctionItem[0]);
    }

    @Override
    public synchronized boolean bid(int userID, int itemID, int price) {
        // TODO:
        // - If item missing OR user unknown OR item already closed -> return false.
        // - If price > current highestBid AND price >= reservePrice:
        // - Update highestBid and return true.
        // - Otherwise, return false to indicate unsuccessful bid.

        if(!Items.containsKey(itemID) || !Users.containsKey(userID)){
            return false;
        }

        AuctionState auction = Items.get(itemID);
        if(!auction.getIsClosed() && auction.getHighestBid() < price && auction.getReservePrice() <= price){
            auction.setHighestBidAndBidder(userID, price);
            return true;
        }
        return false;
    }

    @Override
    public synchronized AuctionResult closeAuction(int userID, int itemID) {
        // TODO:
        // - Look up item; if missing, return null.
        // - Check owner: only creator can close; if not owner, return null.
        // - Mark item as closed (add to closedItems) and remove from active items map.
        // - Return AuctionResult(itemID, winningUser=userID, price=highestBid).
        if(!Items.containsKey(itemID)){
            return null;
        }
        AuctionState auction = Items.get(itemID);
        if(auction.ownerID != userID){
            return null;
        }
        auction.closeAuction();
        AuctionResult resultOfAuction = new AuctionResult(itemID, auction.getHighestBidder(), auction.getHighestBid());

        return resultOfAuction;
    }
}
