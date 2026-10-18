package com.moviebooking.repository;

import com.moviebooking.model.Seat;
import com.moviebooking.model.SeatStatus;
import com.moviebooking.model.SeatType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Seat inventory and atomic concurrency locking.
 */
@Repository
public class SeatRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Seat> findByShowId(int showId) {
        populateDefaultSeatsIfEmpty(showId);
        String sql = "SELECT * FROM seats WHERE show_id = ? ORDER BY seat_number";
        return jdbcTemplate.query(sql, new SeatRowMapper(), showId);
    }

    public void populateDefaultSeatsIfEmpty(int showId) {
        String checkSql = "SELECT COUNT(*) FROM seats WHERE show_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, showId);
        if (count != null && count > 0) {
            return;
        }

        // Standard 30-seat cinema hall:
        // Rows A-C (Standard ₹200.00)
        // Rows D-E (Premium ₹350.00)
        // Row F (VIP Recliner ₹500.00)
        String insertSql = "INSERT INTO seats (show_id, seat_number, type, status, price, is_auction_eligible) VALUES (?, ?, ?, 'AVAILABLE', ?, ?)";
        List<Object[]> batchArgs = new java.util.ArrayList<>();
        for (char r : new char[]{'A', 'B', 'C'}) {
            for (int i = 1; i <= 5; i++) {
                batchArgs.add(new Object[]{showId, "" + r + i, "STANDARD", 200.00, 0});
            }
        }
        for (char r : new char[]{'D', 'E'}) {
            for (int i = 1; i <= 5; i++) {
                batchArgs.add(new Object[]{showId, "" + r + i, "PREMIUM", 350.00, 0});
            }
        }
        for (int i = 1; i <= 5; i++) {
            boolean auction = (i == 1 || i == 2);
            batchArgs.add(new Object[]{showId, "F" + i, "RECLINER", 500.00, auction ? 1 : 0});
        }
        jdbcTemplate.batchUpdate(insertSql, batchArgs);
    }

    public void populateAllMissingSeats() {
        List<Integer> showIds = jdbcTemplate.queryForList("SELECT show_id FROM shows", Integer.class);
        for (Integer showId : showIds) {
            populateDefaultSeatsIfEmpty(showId);
        }
    }

    public Optional<Seat> findById(int seatId) {
        String sql = "SELECT * FROM seats WHERE seat_id = ?";
        List<Seat> seats = jdbcTemplate.query(sql, new SeatRowMapper(), seatId);
        return seats.isEmpty() ? Optional.empty() : Optional.of(seats.get(0));
    }

    public List<Seat> findByIds(List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return Collections.emptyList();
        String inSql = String.join(",", Collections.nCopies(seatIds.size(), "?"));
        String sql = "SELECT * FROM seats WHERE seat_id IN (" + inSql + ") ORDER BY seat_number";
        return jdbcTemplate.query(sql, new SeatRowMapper(), seatIds.toArray());
    }

    public boolean lockSeat(int seatId) {
        String sql = "UPDATE seats SET status = 'LOCKED' WHERE seat_id = ? AND status = 'AVAILABLE'";
        int rowsUpdated = jdbcTemplate.update(sql, seatId);
        return rowsUpdated > 0;
    }

    public boolean unlockSeat(int seatId) {
        String sql = "UPDATE seats SET status = 'AVAILABLE' WHERE seat_id = ? AND status = 'LOCKED'";
        int rowsUpdated = jdbcTemplate.update(sql, seatId);
        return rowsUpdated > 0;
    }

    public boolean reserveSeat(int seatId) {
        String sql = "UPDATE seats SET status = 'RESERVED' WHERE seat_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, seatId);
        return rowsUpdated > 0;
    }

    public boolean markAuctionEligible(int seatId, boolean eligible) {
        String sql = "UPDATE seats SET is_auction_eligible = ?, status = 'AVAILABLE' WHERE seat_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, eligible ? 1 : 0, seatId);
        return rowsUpdated > 0;
    }

    private static class SeatRowMapper implements RowMapper<Seat> {
        @Override
        public Seat mapRow(ResultSet rs, int rowNum) throws SQLException {
            Seat seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setShowId(rs.getInt("show_id"));
            seat.setSeatNumber(rs.getString("seat_number"));

            String typeStr = rs.getString("type");
            try {
                seat.setSeatType(SeatType.valueOf(typeStr.toUpperCase()));
            } catch (Exception e) {
                seat.setSeatType(SeatType.STANDARD);
            }

            String statusStr = rs.getString("status");
            try {
                seat.setStatus(SeatStatus.valueOf(statusStr.toUpperCase()));
            } catch (Exception e) {
                seat.setStatus(SeatStatus.AVAILABLE);
            }

            seat.setPrice(rs.getDouble("price"));
            seat.setAuctionEligible(rs.getBoolean("is_auction_eligible"));
            return seat;
        }
    }
}
