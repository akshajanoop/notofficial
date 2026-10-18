package com.moviebooking.controller;

import com.moviebooking.model.Booking;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowRepository;
import com.moviebooking.service.BookingService;
import com.moviebooking.service.CreditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controller managing the Checkout, Credit Redemption, and Confirmation flow.
 */
@Controller
public class BookingController {

    private final BookingService bookingService;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final CreditService creditService;

    public BookingController(BookingService bookingService,
                             ShowRepository showRepository,
                             SeatRepository seatRepository,
                             BookingRepository bookingRepository,
                             CreditService creditService) {
        this.bookingService = bookingService;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.creditService = creditService;
    }

    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Integer> seatIds = (List<Integer>) session.getAttribute("lockedSeatIds");
        Integer showId = (Integer) session.getAttribute("currentShowId");

        if (seatIds == null || seatIds.isEmpty() || showId == null) {
            return "redirect:/";
        }

        Optional<Show> showOpt = showRepository.findById(showId);
        if (showOpt.isEmpty()) {
            return "redirect:/";
        }

        List<Seat> seats = seatRepository.findByIds(seatIds);
        double subtotal = bookingService.calculateSubtotal(seats);
        int creditBalance = creditService.getBalance(userId);

        model.addAttribute("show", showOpt.get());
        model.addAttribute("seats", seats);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("creditBalance", creditBalance);

        return "checkout";
    }

    @PostMapping("/checkout/confirm")
    public String confirmBooking(@RequestParam(value = "creditsToRedeem", defaultValue = "0") int creditsToRedeem,
                                 HttpSession session,
                                 Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<Integer> seatIds = (List<Integer>) session.getAttribute("lockedSeatIds");
        Integer showId = (Integer) session.getAttribute("currentShowId");

        if (seatIds == null || seatIds.isEmpty() || showId == null) {
            return "redirect:/";
        }

        try {
            Booking booking = bookingService.confirmBooking(userId, showId, seatIds, creditsToRedeem);
            // Clear session locks
            session.removeAttribute("lockedSeatIds");
            session.removeAttribute("currentShowId");
            return "redirect:/confirmation/" + booking.getBookingId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation/{bookingId}")
    public String viewConfirmation(@PathVariable("bookingId") int bookingId,
                                   HttpSession session,
                                   Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return "redirect:/";
        }

        Booking booking = bookingOpt.get();
        int earnedCredits = (int) Math.round(booking.getTotalAmount() * 0.10);
        int updatedBalance = creditService.getBalance(userId);

        model.addAttribute("booking", booking);
        model.addAttribute("earnedCredits", earnedCredits);
        model.addAttribute("currentBalance", updatedBalance);

        return "confirmation";
    }
}
