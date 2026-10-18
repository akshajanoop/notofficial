package com.moviebooking.model;

/**
 * Represents a registered user in the Movie Ticket Booking System.
 * Simple POJO with login() helper method.
 */
public class User {

    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String city;

    // --- Constructors ---

    public User() {}

    public User(int userId, String name, String email, String password, String phone, String city) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.city     = city;
    }

    /**
     * Authenticates user against candidate password.
     */
    public boolean login(String candidatePassword) {
        return candidatePassword != null && candidatePassword.equals(this.password);
    }

    // --- Getters and Setters ---

    public int    getUserId()            { return userId; }
    public void   setUserId(int userId)  { this.userId = userId; }

    public String getName()              { return name; }
    public void   setName(String name)   { this.name = name; }

    public String getEmail()             { return email; }
    public void   setEmail(String e)     { this.email = e; }

    public String getPassword()          { return password; }
    public void   setPassword(String p)  { this.password = p; }

    public String getPhone()             { return phone; }
    public void   setPhone(String p)     { this.phone = p; }

    public String getCity()              { return city; }
    public void   setCity(String c)      { this.city = c; }
}
