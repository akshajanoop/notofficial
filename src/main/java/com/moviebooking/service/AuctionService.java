package com.moviebooking.service;

import com.moviebooking.model.Auction;
import com.moviebooking.repository.AuctionRepository;
import com.moviebooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing live bidding mechanics, ticket reselling, countdown status, and auction expiry resolution.
 */
@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final SeatRepository seatRepository;

    public AuctionService(AuctionRepository auctionRepository, SeatRepository seatRepository) {
        this.auctionRepository = auctionRepository;
        this.seatRepository = seatRepository;
    }

    public Optional<Auction> getAuction(int auctionId) {
        return auctionRepository.findById(auctionId);
    }

    public Optional<Auction> getAuctionBySeat(int seatId) {
        return auctionRepository.findBySeatId(seatId);
    }

    public List<Auction> getActiveAuctions() {
        return auctionRepository.findActiveAuctions();
    }

    /**
     * Resells a booked ticket by creating a new live auction for the seat.
     */
    @Transactional
    public int createResaleAuction(int seatId, double startingBid, int durationMinutes) {
        // Mark seat as auction-eligible and set available for bidding
        seatRepository.markAuctionEligible(seatId, true);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(durationMinutes);
        return auctionRepository.createAuction(seatId, startingBid, endTime);
    }

    /**
     * Submits a higher bid on an active auction.
     */
    @Transactional
    public synchronized Map<String, Object> placeBid(int auctionId, int userId, double bidAmount) {
        Map<String, Object> response = new HashMap<>();

        Optional<Auction> opt = auctionRepository.findById(auctionId);
        if (opt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Auction not found.");
            return response;
        }

        Auction auction = opt.get();
        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus()) || auction.isExpired()) {
            response.put("success", false);
            response.put("message", "Auction has ended.");
            return response;
        }

        if (bidAmount <= auction.getCurrentBid()) {
            response.put("success", false);
            response.put("message", "Bid must be higher than current bid of ₹" + auction.getCurrentBid());
            return response;
        }

        boolean updated = auctionRepository.updateBid(auctionId, bidAmount, userId);
        if (updated) {
            response.put("success", true);
            response.put("message", "Bid placed successfully!");
            response.put("currentBid", bidAmount);
        } else {
            response.put("success", false);
            response.put("message", "Failed to place bid. Another higher bid was placed.");
        }
        return response;
    }

    /**
     * Returns dynamic poll status (current bid, highest bidder name, time remaining).
     */
    public Map<String, Object> getAuctionStatus(int auctionId) {
        Map<String, Object> statusMap = new HashMap<>();
        Optional<Auction> opt = auctionRepository.findById(auctionId);
        if (opt.isPresent()) {
            Auction a = opt.get();
            statusMap.put("auctionId", a.getAuctionId());
            statusMap.put("currentBid", a.getCurrentBid());
            statusMap.put("highestBidderName", a.getHighestBidderName() != null ? a.getHighestBidderName() : "No bids yet");
            statusMap.put("remainingSeconds", a.getRemainingSeconds());
            statusMap.put("status", a.getStatus());
            statusMap.put("isExpired", a.isExpired());
        }
        return statusMap;
    }

    /**
     * Periodically resolves expired auctions: locks winning seats and sets status to RESOLVED.
     */
    @Transactional
    public void resolveExpiredAuctions() {
        List<Auction> expiredList = auctionRepository.findExpiredActiveAuctions();
        for (Auction auction : expiredList) {
            if (auction.getHighestBidderId() != null) {
                auctionRepository.updateStatus(auction.getAuctionId(), "RESOLVED");
                seatRepository.reserveSeat(auction.getSeatId());
                System.out.println("Auction #" + auction.getAuctionId() + " WON by User #" + auction.getHighestBidderId());
            } else {
                auctionRepository.updateStatus(auction.getAuctionId(), "EXPIRED");
            }
        }
    }
}
