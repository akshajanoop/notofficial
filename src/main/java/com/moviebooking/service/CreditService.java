package com.moviebooking.service;

import com.moviebooking.model.CreditTransaction;
import com.moviebooking.repository.CreditRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Loyalty Credit operations (balance checks, earnings, redemptions, ledger logs).
 */
@Service
public class CreditService {

    private final CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public int getBalance(int userId) {
        return creditRepository.getBalance(userId);
    }

    public void earnCredits(int userId, int points, String reason) {
        int current = creditRepository.getBalance(userId);
        int updated = current + points;
        creditRepository.updateBalance(userId, updated);
        creditRepository.addTransaction(new CreditTransaction(userId, "EARN", points, reason, updated));
    }

    public boolean redeemCredits(int userId, int points, String reason) {
        int current = creditRepository.getBalance(userId);
        if (current >= points && points > 0) {
            int updated = current - points;
            creditRepository.updateBalance(userId, updated);
            creditRepository.addTransaction(new CreditTransaction(userId, "REDEEM", points, reason, updated));
            return true;
        }
        return false;
    }

    public List<CreditTransaction> getHistory(int userId) {
        return creditRepository.getTransactionsByUserId(userId);
    }
}
