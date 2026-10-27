package com.moviebooking.repository;

import com.moviebooking.model.Booking;
import com.moviebooking.model.Seat;
import com.moviebooking.model.SeatStatus;
import com.moviebooking.model.SeatType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Bookings and booking-to-seat junction records.
 */
@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, show_id, total_amount, credits_used, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"BOOKING_ID"});
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getShowId());
            ps.setDouble(3, booking.getTotalAmount());
            ps.setInt(4, booking.getCreditsUsed());
            ps.setString(5, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");
            ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
            return ps;
        }, keyHolder);

        int bookingId = 0;
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("BOOKING_ID")) {
            Object idVal = keyHolder.getKeys().get("BOOKING_ID");
            if (idVal instanceof Number) {
                bookingId = ((Number) idVal).intValue();
            }
        } else if (keyHolder.getKey() != null) {
            bookingId = keyHolder.getKey().intValue();
        }
        booking.setBookingId(bookingId);
        return bookingId;
    }

    public void addBookingSeat(int bookingId, int seatId) {
        String sql = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, bookingId, seatId);
    }

    public Optional<Booking> findById(int bookingId) {
        String sql = """
            SELECT b.*, m.title AS movie_title, t.name AS theatre_name
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE b.booking_id = ?
        """;
        List<Booking> list = jdbcTemplate.query(sql, new BookingRowMapper(), bookingId);
        if (list.isEmpty()) return Optional.empty();
        Booking booking = list.get(0);
        booking.setSeats(findSeatsByBookingId(booking.getBookingId()));
        return Optional.of(booking);
    }

    public List<Booking> findByUserId(int userId) {
        String sql = """
            SELECT b.*, m.title AS movie_title, t.name AS theatre_name
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE b.user_id = ?
            ORDER BY b.created_at DESC
        """;
        List<Booking> bookings = jdbcTemplate.query(sql, new BookingRowMapper(), userId);
        for (Booking b : bookings) {
            b.setSeats(findSeatsByBookingId(b.getBookingId()));
        }
        return bookings;
    }

    public List<Seat> findSeatsByBookingId(int bookingId) {
        String sql = """
            SELECT st.*
            FROM seats st
            JOIN booking_seats bs ON st.seat_id = bs.seat_id
            WHERE bs.booking_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Seat seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setShowId(rs.getInt("show_id"));
            seat.setSeatNumber(rs.getString("seat_number"));
            try {
                seat.setSeatType(SeatType.valueOf(rs.getString("type").toUpperCase()));
            } catch (Exception e) {
                seat.setSeatType(SeatType.STANDARD);
            }
            try {
                seat.setStatus(SeatStatus.valueOf(rs.getString("status").toUpperCase()));
            } catch (Exception e) {
                seat.setStatus(SeatStatus.RESERVED);
            }
            seat.setPrice(rs.getDouble("price"));
            seat.setAuctionEligible(rs.getBoolean("is_auction_eligible"));
            return seat;
        }, bookingId);
    }

    public List<String> findGenresBookedByUser(int userId) {
        String sql = """
            SELECT DISTINCT m.genre
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            WHERE b.user_id = ? AND m.genre IS NOT NULL
        """;
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    /**
     * Retrieve all bookings across the entire system for Administrator review and management.
     */
    public List<Booking> findAll() {
        String sql = """
            SELECT b.*, m.title AS movie_title, t.name AS theatre_name
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            ORDER BY b.created_at DESC
        """;
        List<Booking> bookings = jdbcTemplate.query(sql, new BookingRowMapper());
        for (Booking b : bookings) {
            b.setSeats(findSeatsByBookingId(b.getBookingId()));
        }
        return bookings;
    }

    /**
     * Update the status of an existing customer booking (e.g. CONFIRMED, CANCELLED, REFUNDED).
     */
    public void updateStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        jdbcTemplate.update(sql, status, bookingId);

        // If marked CANCELLED or REFUNDED, automatically release seats back to AVAILABLE
        if ("CANCELLED".equalsIgnoreCase(status) || "REFUNDED".equalsIgnoreCase(status)) {
            List<Seat> seats = findSeatsByBookingId(bookingId);
            for (Seat s : seats) {
                jdbcTemplate.update("UPDATE seats SET status = 'AVAILABLE' WHERE seat_id = ?", s.getSeatId());
            }
        }
    }

    /**
     * Completely remove a booking from the database and free up all reserved seats.
     */
    public void delete(int bookingId) {
        List<Seat> seats = findSeatsByBookingId(bookingId);
        for (Seat s : seats) {
            jdbcTemplate.update("UPDATE seats SET status = 'AVAILABLE' WHERE seat_id = ?", s.getSeatId());
        }
        jdbcTemplate.update("DELETE FROM booking_seats WHERE booking_id = ?", bookingId);
        jdbcTemplate.update("DELETE FROM bookings WHERE booking_id = ?", bookingId);
    }

    private static class BookingRowMapper implements RowMapper<Booking> {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            Booking booking = new Booking();
            booking.setBookingId(rs.getInt("booking_id"));
            booking.setUserId(rs.getInt("user_id"));
            booking.setShowId(rs.getInt("show_id"));
            booking.setTotalAmount(rs.getDouble("total_amount"));
            booking.setCreditsUsed(rs.getInt("credits_used"));
            booking.setStatus(rs.getString("status"));
            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                booking.setCreatedAt(ts.toLocalDateTime());
            }
            booking.setMovieTitle(rs.getString("movie_title"));
            booking.setTheatreName(rs.getString("theatre_name"));
            return booking;
        }
    }
}
