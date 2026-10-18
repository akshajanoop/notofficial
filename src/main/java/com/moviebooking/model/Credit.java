package com.moviebooking.model;

/**
 * User Loyalty Credits Model.
 * Provides earn() and redeem() methods for reward point management.
 */
public class Credit {

    private int userId;
    private int balance;

    public Credit() {}

    public Credit(int userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    /**
     * Add reward points to user balance.
     */
    public void earn(int points) {
        if (points > 0) {
            this.balance += points;
        }
    }

    /**
     * Deduct reward points from user balance if sufficient points exist.
     */
    public boolean redeem(int points) {
        if (points <= 0) return false;
        if (this.balance >= points) {
            this.balance -= points;
            return true;
        }
        return false;
    }

    // --- Getters & Setters ---

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }
}
