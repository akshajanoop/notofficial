package com.moviebooking.model;

/**
 * Represents a single physical seat inside a show's auditorium.
 */
public class Seat {

    private int seatId;
    private int showId;
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus status;
    private double price;
    private boolean isAuctionEligible;

    public Seat() {}

    public Seat(int seatId, int showId, String seatNumber, SeatType seatType,
                SeatStatus status, double price, boolean isAuctionEligible) {
        this.seatId            = seatId;
        this.showId            = showId;
        this.seatNumber        = seatNumber;
        this.seatType          = seatType;
        this.status            = status;
        this.price             = price;
        this.isAuctionEligible = isAuctionEligible;
    }

    /**
     * Reserves this seat.
     */
    public void reserve() {
        this.status = SeatStatus.RESERVED;
    }

    // --- Getters and Setters ---

    public int        getSeatId()                           { return seatId; }
    public void       setSeatId(int seatId)                 { this.seatId = seatId; }

    public int        getShowId()                           { return showId; }
    public void       setShowId(int showId)                 { this.showId = showId; }

    public String     getSeatNumber()                       { return seatNumber; }
    public void       setSeatNumber(String seatNumber)      { this.seatNumber = seatNumber; }

    public SeatType   getSeatType()                         { return seatType; }
    public void       setSeatType(SeatType seatType)        { this.seatType = seatType; }

    public SeatStatus getStatus()                           { return status; }
    public void       setStatus(SeatStatus status)          { this.status = status; }

    public double     getPrice()                            { return price; }
    public void       setPrice(double price)                { this.price = price; }

    public boolean    isAuctionEligible()                   { return isAuctionEligible; }
    public void       setAuctionEligible(boolean e)         { this.isAuctionEligible = e; }
}
