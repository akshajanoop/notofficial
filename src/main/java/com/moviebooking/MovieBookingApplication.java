package com.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Movie Ticket Booking System - Main Application Entry Point
 * 
 * Features included:
 * 1. Interactive Seat Selection with dynamic pricing & locking.
 * 2. Live Ticket Auctioning with real-time bidding & countdown resolution.
 * 3. Personalized Movie & Theatre Recommendation engine.
 * 4. Loyalty Credits reward & redemption program.
 * 5. Simple, clear JDBC-based repository architecture.
 */
@SpringBootApplication
@EnableScheduling
public class MovieBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBookingApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner initShowSeats(com.moviebooking.repository.SeatRepository seatRepository) {
        return args -> {
            seatRepository.populateAllMissingSeats();
            System.out.println("=========================================================");
            System.out.println(" Movie Ticket Booking System is running successfully!   ");
            System.out.println(" Open your browser: http://localhost:8080               ");
            System.out.println(" H2 Database Console: http://localhost:8080/h2-console  ");
            System.out.println("=========================================================");
        };
    }
}
