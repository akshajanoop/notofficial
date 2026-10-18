package com.moviebooking.service;

import com.moviebooking.model.Booking;
import com.moviebooking.model.CreditTransaction;
import com.moviebooking.model.Seat;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.CreditRepository;
import com.moviebooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service orchestrating seat locking, total calculation, booking confirmation,
 * and loyalty credit application.
 */
@Service
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final CreditRepository creditRepository;

    public BookingService(SeatRepository seatRepository,
                          BookingRepository bookingRepository,
                          CreditRepository creditRepository) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.creditRepository = creditRepository;
    }

    /**
     * Atomically locks all selected seats for the current user.
     * If any seat fails to lock (already taken), all previously locked seats are rolled back.
     */
    @Transactional
    public boolean lockSeats(List<Integer> seatIds) {
        List<Integer> lockedSoFar = new ArrayList<>();
        for (Integer seatId : seatIds) {
            boolean success = seatRepository.lockSeat(seatId);
            if (!success) {
                // Concurrency rollback: unlock what we locked
                for (Integer lockedId : lockedSoFar) {
                    seatRepository.unlockSeat(lockedId);
                }
                return false;
            }
            lockedSoFar.add(seatId);
        }
        return true;
    }

    /**
     * Unlocks a list of seats when a user leaves the checkout or cancels.
     */
    @Transactional
    public void unlockSeats(List<Integer> seatIds) {
        if (seatIds != null) {
            for (Integer seatId : seatIds) {
                seatRepository.unlockSeat(seatId);
            }
        }
    }

    /**
     * Calculates the subtotal, credit discount, and final amount.
     */
    public double calculateSubtotal(List<Seat> seats) {
        return seats.stream().mapToDouble(Seat::getPrice).sum();
    }

    /**
     * Confirms the booking:
     * 1. Validates credit redemption.
     * 2. Persists the booking record.
     * 3. Links seats to the booking.
     * 4. Updates seat status to RESERVED.
     * 5. Deducts used credits & records credit transaction.
     * 6. Awards 10% loyalty credits on the final amount paid.
     */
    @Transactional
    public Booking confirmBooking(int userId, int showId, List<Integer> seatIds, int creditsToUse) {
        List<Seat> seats = seatRepository.findByIds(seatIds);
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("No valid seats selected for booking.");
        }

        double subtotal = calculateSubtotal(seats);
        int currentBalance = creditRepository.getBalance(userId);

        // Sanitize credits used (cannot exceed user balance or ticket total)
        int effectiveCredits = Math.min(creditsToUse, currentBalance);
        effectiveCredits = (int) Math.min(effectiveCredits, subtotal);

        double finalAmount = Math.max(0.0, subtotal - effectiveCredits);

        // Create booking header
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setShowId(showId);
        booking.setTotalAmount(finalAmount);
        booking.setCreditsUsed(effectiveCredits);
        booking.setStatus("CONFIRMED");

        int bookingId = bookingRepository.save(booking);

        // Link and finalize seats
        for (Integer seatId : seatIds) {
            bookingRepository.addBookingSeat(bookingId, seatId);
            seatRepository.reserveSeat(seatId);
        }

        // Apply Credit Deductions
        int updatedBalance = currentBalance;
        if (effectiveCredits > 0) {
            updatedBalance -= effectiveCredits;
            creditRepository.updateBalance(userId, updatedBalance);
            creditRepository.addTransaction(new CreditTransaction(
                userId, "REDEEM", effectiveCredits,
                "Redeemed for Booking #" + bookingId, updatedBalance
            ));
        }

        // Earn Loyalty Credits (10% of final cash amount spent, rounded)
        int earnedCredits = (int) Math.round(finalAmount * 0.10);
        if (earnedCredits > 0) {
            updatedBalance += earnedCredits;
            creditRepository.updateBalance(userId, updatedBalance);
            creditRepository.addTransaction(new CreditTransaction(
                userId, "EARN", earnedCredits,
                "Earned from Booking #" + bookingId, updatedBalance
            ));
        }

        booking.setSeats(seats);
        return bookingRepository.findById(bookingId).orElse(booking);
    }
}
