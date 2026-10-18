package com.moviebooking.model;

import java.time.LocalDateTime;

/**
 * Represents a scheduled screening of a Movie at a Theatre.
 * One movie can have multiple shows at different theatres and times.
 */
public class Show {

    /** Unique identifier for this show */
    private int showId;

    /** Foreign key linking to the Movie being screened */
    private int movieId;

    /** Foreign key linking to the Theatre where this show plays */
    private int theatreId;

    /** Date and time when the show starts */
    private LocalDateTime showTime;

    /** Convenience field populated by a JOIN - the movie's title */
    private String movieTitle;

    /** Convenience field populated by a JOIN - the theatre's name */
    private String theatreName;

    /** Number of seats currently available (populated by subquery) */
    private int availableSeatCount;

    // --- Constructors ---

    public Show() {}

    // --- Getters and Setters ---

    public int           getShowId()                         { return showId; }
    public void          setShowId(int showId)               { this.showId = showId; }

    public int           getMovieId()                        { return movieId; }
    public void          setMovieId(int movieId)             { this.movieId = movieId; }

    public int           getTheatreId()                      { return theatreId; }
    public void          setTheatreId(int theatreId)         { this.theatreId = theatreId; }

    public LocalDateTime getShowTime()                       { return showTime; }
    public void          setShowTime(LocalDateTime showTime) { this.showTime = showTime; }

    public java.time.LocalDate getShowDate()                 { return showTime != null ? showTime.toLocalDate() : null; }
    public String              getFormattedTime()            { return showTime != null ? showTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")) : ""; }

    public String        getMovieTitle()                     { return movieTitle; }
    public void          setMovieTitle(String t)             { this.movieTitle = t; }

    public String        getTheatreName()                    { return theatreName; }
    public void          setTheatreName(String n)            { this.theatreName = n; }

    public int           getAvailableSeatCount()             { return availableSeatCount; }
    public void          setAvailableSeatCount(int c)        { this.availableSeatCount = c; }
    public int           getAvailableSeats()                 { return availableSeatCount; }
}
