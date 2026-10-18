package com.moviebooking.controller;

import com.moviebooking.model.Auction;
import com.moviebooking.model.Seat;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.service.AuctionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller handling the Live Ticket Auctioning Screen, Resale Listings, and REST bidding.
 */
@Controller
public class AuctionController {

    private final AuctionService auctionService;
    private final SeatRepository seatRepository;
    private final JdbcTemplate jdbcTemplate;

    public AuctionController(AuctionService auctionService, SeatRepository seatRepository, JdbcTemplate jdbcTemplate) {
        this.auctionService = auctionService;
        this.seatRepository = seatRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Dedicated Live Auction & Reselling Hub.
     * Displays all active auctions AND provides a creation form to put ANY seat up for live auction.
     */
    @GetMapping("/auctions")
    public String listAuctions(Model model) {
        List<Auction> activeAuctions = auctionService.getActiveAuctions();
        
        // Fetch all available seats with show & movie info so any seat can be auctioned directly from here
        String sql = """
            SELECT st.seat_id, st.seat_number, st.type, st.price, m.title AS movie_title, t.name AS theatre_name, s.show_time, s.show_date
            FROM seats st
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE st.status = 'AVAILABLE'
            ORDER BY s.show_date, s.show_time, st.seat_number
        """;
        List<Map<String, Object>> availableSeats = jdbcTemplate.queryForList(sql);

        model.addAttribute("auctions", activeAuctions);
        model.addAttribute("availableSeats", availableSeats);
        return "auctions";
    }

    /**
     * Individual Auction live bidding room.
     */
    @GetMapping("/auction/{auctionId}")
    public String viewAuction(@PathVariable("auctionId") int auctionId, Model model, HttpSession session) {
        Optional<Auction> auctionOpt = auctionService.getAuction(auctionId);
        if (auctionOpt.isEmpty()) {
            return "redirect:/auctions";
        }

        Auction auction = auctionOpt.get();
        Optional<Seat> seatOpt = seatRepository.findById(auction.getSeatId());

        model.addAttribute("auction", auction);
        model.addAttribute("seat", seatOpt.orElse(null));

        return "auction";
    }

    /**
     * Endpoint to list ANY seat for live auction (from the auction tab).
     */
    @PostMapping("/auction/create")
    public String createAuctionFromHub(@RequestParam("seatId") int seatId,
                                       @RequestParam("startingBid") double startingBid,
                                       @RequestParam(value = "durationMinutes", defaultValue = "60") int durationMinutes,
                                       HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        int auctionId = auctionService.createResaleAuction(seatId, startingBid, durationMinutes);
        return "redirect:/auction/" + auctionId;
    }

    /**
     * REST endpoint for submitting bids.
     */
    @PostMapping("/api/auction/{auctionId}/bid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> placeBid(@PathVariable("auctionId") int auctionId,
                                                        @RequestBody Map<String, Object> payload,
                                                        HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Collections.singletonMap("redirect", "/login"));
        }

        double bidAmount;
        try {
            bidAmount = Double.parseDouble(payload.get("bidAmount").toString());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Invalid bid amount."));
        }

        Map<String, Object> result = auctionService.placeBid(auctionId, userId, bidAmount);
        return ResponseEntity.ok(result);
    }

    /**
     * REST polling endpoint for dynamic countdown and current bid updates.
     */
    @GetMapping("/api/auction/{auctionId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAuctionStatus(@PathVariable("auctionId") int auctionId) {
        Map<String, Object> status = auctionService.getAuctionStatus(auctionId);
        return ResponseEntity.ok(status);
    }
}
