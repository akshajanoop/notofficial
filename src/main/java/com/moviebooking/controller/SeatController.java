package com.moviebooking.controller;

import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowRepository;
import com.moviebooking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Controller handling Seat Selection Map with realistic theater row grouping and viewing quality metrics.
 */
@Controller
public class SeatController {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingService bookingService;

    public SeatController(ShowRepository showRepository,
                          SeatRepository seatRepository,
                          BookingService bookingService) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/shows/{showId}/seats")
    public String viewSeatMap(@PathVariable("showId") int showId, Model model, HttpSession session) {
        // Enforce role rule: Administrators cannot book tickets
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin?msg=admin_cannot_book";
        }

        Optional<Show> showOpt = showRepository.findById(showId);
        if (showOpt.isEmpty()) {
            return "redirect:/";
        }

        Show show = showOpt.get();
        List<Seat> seats = seatRepository.findByShowId(showId);

        // Group seats by their row letter (e.g., 'A', 'B', 'C', 'D', 'E', 'F')
        Map<String, List<Seat>> seatsByRow = new TreeMap<>();
        for (Seat s : seats) {
            String rowLetter = s.getSeatNumber().substring(0, 1);
            seatsByRow.computeIfAbsent(rowLetter, k -> new ArrayList<>()).add(s);
        }

        model.addAttribute("show", show);
        model.addAttribute("seatsByRow", seatsByRow);
        model.addAttribute("seats", seats);

        return "seats";
    }

    @PostMapping("/api/seats/lock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> lockSeats(@RequestBody Map<String, Object> payload, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Enforce role rule: Administrators cannot book tickets
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            response.put("success", false);
            response.put("message", "Administrators are restricted from booking customer tickets. Please use a customer account to purchase tickets.");
            return ResponseEntity.ok(response);
        }

        if (session.getAttribute("userId") == null) {
            response.put("success", false);
            response.put("redirect", "/login");
            return ResponseEntity.ok(response);
        }

        @SuppressWarnings("unchecked")
        List<Integer> seatIds = (List<Integer>) payload.get("seatIds");
        Integer showId = (Integer) payload.get("showId");

        if (seatIds == null || seatIds.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select at least one seat.");
            return ResponseEntity.ok(response);
        }

        boolean locked = bookingService.lockSeats(seatIds);
        if (locked) {
            session.setAttribute("lockedSeatIds", seatIds);
            session.setAttribute("currentShowId", showId);
            response.put("success", true);
            response.put("redirect", "/checkout");
        } else {
            response.put("success", false);
            response.put("message", "One or more selected seats were just taken. Please choose another seat.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/seats/unlock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unlockSeats(@RequestBody Map<String, Object> payload, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Integer> seatIds = (List<Integer>) payload.get("seatIds");
        bookingService.unlockSeats(seatIds);
        session.removeAttribute("lockedSeatIds");
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
}
