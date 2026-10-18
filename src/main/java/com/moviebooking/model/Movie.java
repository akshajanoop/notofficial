package com.moviebooking.model;

import java.util.List;

/**
 * Represents a Movie available for booking with Director, Cast & Crew metadata.
 */
public class Movie {

    private int movieId;
    private String title;
    private String genre;
    private int durationMinutes;
    private String posterUrl;
    private String description;
    private String language;
    private double rating;
    private String director;
    private String castMembers;
    private String crewMembers;

    private List<Show> shows;

    public Movie() {}

    public Movie(int movieId, String title, String genre, int durationMinutes,
                 String posterUrl, String description, String language, double rating,
                 String director, String castMembers, String crewMembers) {
        this.movieId         = movieId;
        this.title           = title;
        this.genre           = genre;
        this.durationMinutes = durationMinutes;
        this.posterUrl       = posterUrl;
        this.description     = description;
        this.language        = language;
        this.rating          = rating;
        this.director        = director;
        this.castMembers     = castMembers;
        this.crewMembers     = crewMembers;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    // --- Getters and Setters ---

    public int    getMovieId()                       { return movieId; }
    public void   setMovieId(int movieId)            { this.movieId = movieId; }

    public String getTitle()                         { return title; }
    public void   setTitle(String title)             { this.title = title; }

    public String getGenre()                         { return genre; }
    public void   setGenre(String genre)             { this.genre = genre; }

    public int    getDurationMinutes()               { return durationMinutes; }
    public void   setDurationMinutes(int d)          { this.durationMinutes = d; }

    public String getPosterUrl()                     { return posterUrl; }
    public void   setPosterUrl(String posterUrl)     { this.posterUrl = posterUrl; }

    public String getDescription()                   { return description; }
    public void   setDescription(String d)           { this.description = d; }

    public String getLanguage()                      { return language; }
    public void   setLanguage(String language)       { this.language = language; }

    public double getRating()                        { return rating; }
    public void   setRating(double rating)           { this.rating = rating; }

    public String getDirector()                      { return director; }
    public void   setDirector(String director)       { this.director = director; }

    public String getCastMembers()                   { return castMembers; }
    public void   setCastMembers(String castMembers) { this.castMembers = castMembers; }

    public String getCrewMembers()                   { return crewMembers; }
    public void   setCrewMembers(String crewMembers) { this.crewMembers = crewMembers; }
}
