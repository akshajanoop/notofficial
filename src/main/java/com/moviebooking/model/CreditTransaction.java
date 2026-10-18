package com.moviebooking.model;

import java.time.LocalDateTime;

/**
 * Immutable transaction ledger entry for credit earnings and redemptions.
 */
public class CreditTransaction {

    private int transactionId;
    private int userId;
    private String transactionType; // 'EARN' or 'REDEEM'
    private int amount;
    private String description;
    private int balanceAfter;
    private LocalDateTime createdAt;

    public CreditTransaction() {}

    public CreditTransaction(int userId, String transactionType, int amount, String description, int balanceAfter) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
