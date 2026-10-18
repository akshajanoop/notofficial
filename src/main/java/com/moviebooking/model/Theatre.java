package com.moviebooking.model;

import java.util.List;

/**
 * Represents a physical Theatre/Cinema hall with GPS Geolocation coordinates.
 */
public class Theatre {

    private int theatreId;
    private String name;
    private String city;
    private String address;
    private double latitude;
    private double longitude;
    private Double distanceKm; // Calculated distance from user's current GPS location

    private List<Show> shows;

    public Theatre() {}

    public Theatre(int theatreId, String name, String city, String address, double latitude, double longitude) {
        this.theatreId = theatreId;
        this.name      = name;
        this.city      = city;
        this.address   = address;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    // --- Getters and Setters ---

    public int    getTheatreId()              { return theatreId; }
    public void   setTheatreId(int theatreId) { this.theatreId = theatreId; }

    public String getName()                   { return name; }
    public void   setName(String name)        { this.name = name; }

    public String getCity()                   { return city; }
    public void   setCity(String city)        { this.city = city; }

    public String getAddress()                { return address; }
    public void   setAddress(String address)  { this.address = address; }

    public double getLatitude()               { return latitude; }
    public void   setLatitude(double lat)     { this.latitude = lat; }

    public double getLongitude()              { return longitude; }
    public void   setLongitude(double lng)    { this.longitude = lng; }

    public Double getDistanceKm()             { return distanceKm; }
    public void   setDistanceKm(Double d)     { this.distanceKm = d; }
}
