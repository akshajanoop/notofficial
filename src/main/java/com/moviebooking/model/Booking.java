package com.moviebooking.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a ticket booking made by a User for a specific Show.
 */
public class Booking {

    private int bookingId;
    private int userId;
    private int showId;
    private double totalAmount;
    private int creditsUsed;
    private String status; // 'PENDING', 'CONFIRMED', 'CANCELLED'
    private LocalDateTime createdAt;

    // Transient presentation fields
    private String movieTitle;
    private String theatreName;
    private List<Seat> seats;

    public Booking() {}

    /**
     * Confirms the booking status.
     */
    public void confirm() {
        this.status = "CONFIRMED";
    }

    // --- Getters and Setters ---

    public int           getBookingId()                      { return bookingId; }
    public void          setBookingId(int bookingId)         { this.bookingId = bookingId; }

    public int           getUserId()                         { return userId; }
    public void          setUserId(int userId)               { this.userId = userId; }

    public int           getShowId()                         { return showId; }
    public void          setShowId(int showId)               { this.showId = showId; }

    public double        getTotalAmount()                    { return totalAmount; }
    public void          setTotalAmount(double totalAmount)  { this.totalAmount = totalAmount; }

    public int           getCreditsUsed()                    { return creditsUsed; }
    public void          setCreditsUsed(int creditsUsed)     { this.creditsUsed = creditsUsed; }

    public String        getStatus()                         { return status; }
    public void          setStatus(String status)            { this.status = status; }

    public LocalDateTime getCreatedAt()                      { return createdAt; }
    public void          setCreatedAt(LocalDateTime c)       { this.createdAt = c; }

    public String        getMovieTitle()                     { return movieTitle; }
    public void          setMovieTitle(String t)             { this.movieTitle = t; }

    public String        getTheatreName()                    { return theatreName; }
    public void          setTheatreName(String n)            { this.theatreName = n; }

    public List<Seat>    getSeats()                          { return seats; }
    public void          setSeats(List<Seat> seats)          { this.seats = seats; }
}
