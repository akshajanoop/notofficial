package com.moviebooking.controller;

import com.moviebooking.model.Booking;
import com.moviebooking.model.CreditTransaction;
import com.moviebooking.model.User;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.service.CreditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

/**
 * Controller for User Account Profile Screen (User details, loyalty balance, and booking history).
 */
@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CreditService creditService;

    public ProfileController(UserRepository userRepository,
                             BookingRepository bookingRepository,
                             CreditService creditService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.creditService = creditService;
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        int creditBalance = creditService.getBalance(userId);
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        List<CreditTransaction> creditHistory = creditService.getHistory(userId);

        model.addAttribute("user", user);
        model.addAttribute("creditBalance", creditBalance);
        model.addAttribute("bookings", bookings);
        model.addAttribute("creditHistory", creditHistory);

        return "profile";
    }
}
