package com.moviebooking.model;

import java.time.LocalDateTime;

/**
 * Live Ticket Auction for high-demand or premium seats.
 * Users can place bids before endTime. When expired, the highest bidder wins the seat.
 */
public class Auction {

    private int auctionId;
    private int seatId;
    private double currentBid;
    private Integer highestBidderId;
    private LocalDateTime endTime;
    private String status; // 'ACTIVE', 'RESOLVED', 'EXPIRED'

    // Transient attributes for UI presentation
    private String seatNumber;
    private String movieTitle;
    private String theatreName;
    private String highestBidderName;
    private long remainingSeconds;

    public Auction() {}

    /**
     * Attempts to place a bid on this auction.
     * Throws IllegalArgumentException if bid amount is not higher than current bid.
     */
    public boolean placeBid(int bidderId, double newBidAmount) {
        if (!"ACTIVE".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Auction is no longer active.");
        }
        if (newBidAmount <= this.currentBid) {
            throw new IllegalArgumentException("Bid amount must be strictly greater than current bid ₹" + this.currentBid);
        }
        this.currentBid = newBidAmount;
        this.highestBidderId = bidderId;
        return true;
    }

    public boolean isExpired() {
        return endTime != null && LocalDateTime.now().isAfter(endTime);
    }

    // --- Getters & Setters ---

    public int getAuctionId() { return auctionId; }
    public void setAuctionId(int auctionId) { this.auctionId = auctionId; }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public double getCurrentBid() { return currentBid; }
    public void setCurrentBid(double currentBid) { this.currentBid = currentBid; }

    public Integer getHighestBidderId() { return highestBidderId; }
    public void setHighestBidderId(Integer highestBidderId) { this.highestBidderId = highestBidderId; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public String getHighestBidderName() { return highestBidderName; }
    public void setHighestBidderName(String highestBidderName) { this.highestBidderName = highestBidderName; }

    public long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
}
