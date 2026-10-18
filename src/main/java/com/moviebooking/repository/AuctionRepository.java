package com.moviebooking.repository;

import com.moviebooking.model.Auction;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Live Ticket Auctioning and Ticket Reselling.
 */
@Repository
public class AuctionRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createAuction(int seatId, double startingBid, LocalDateTime endTime) {
        String sql = "INSERT INTO auctions (seat_id, current_bid, highest_bidder_id, end_time, status) VALUES (?, ?, NULL, ?, 'ACTIVE')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, seatId);
            ps.setDouble(2, startingBid);
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public Optional<Auction> findById(int auctionId) {
        String sql = """
            SELECT a.*, st.seat_number, m.title AS movie_title, t.name AS theatre_name, u.name AS bidder_name
            FROM auctions a
            JOIN seats st ON a.seat_id = st.seat_id
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            LEFT JOIN users u ON a.highest_bidder_id = u.user_id
            WHERE a.auction_id = ?
        """;
        List<Auction> list = jdbcTemplate.query(sql, new AuctionRowMapper(), auctionId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Auction> findBySeatId(int seatId) {
        String sql = """
            SELECT a.*, st.seat_number, m.title AS movie_title, t.name AS theatre_name, u.name AS bidder_name
            FROM auctions a
            JOIN seats st ON a.seat_id = st.seat_id
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            LEFT JOIN users u ON a.highest_bidder_id = u.user_id
            WHERE a.seat_id = ? AND a.status = 'ACTIVE'
        """;
        List<Auction> list = jdbcTemplate.query(sql, new AuctionRowMapper(), seatId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<Auction> findActiveAuctions() {
        String sql = """
            SELECT a.*, st.seat_number, m.title AS movie_title, t.name AS theatre_name, u.name AS bidder_name
            FROM auctions a
            JOIN seats st ON a.seat_id = st.seat_id
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            LEFT JOIN users u ON a.highest_bidder_id = u.user_id
            WHERE a.status = 'ACTIVE'
            ORDER BY a.end_time ASC
        """;
        return jdbcTemplate.query(sql, new AuctionRowMapper());
    }

    public boolean updateBid(int auctionId, double newBid, int userId) {
        String sql = "UPDATE auctions SET current_bid = ?, highest_bidder_id = ? WHERE auction_id = ? AND status = 'ACTIVE' AND current_bid < ?";
        int updated = jdbcTemplate.update(sql, newBid, userId, auctionId, newBid);
        return updated > 0;
    }

    public void updateStatus(int auctionId, String status) {
        String sql = "UPDATE auctions SET status = ? WHERE auction_id = ?";
        jdbcTemplate.update(sql, status, auctionId);
    }

    public List<Auction> findExpiredActiveAuctions() {
        String sql = """
            SELECT a.*, st.seat_number, m.title AS movie_title, t.name AS theatre_name, u.name AS bidder_name
            FROM auctions a
            JOIN seats st ON a.seat_id = st.seat_id
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            LEFT JOIN users u ON a.highest_bidder_id = u.user_id
            WHERE a.status = 'ACTIVE' AND a.end_time <= CURRENT_TIMESTAMP
        """;
        return jdbcTemplate.query(sql, new AuctionRowMapper());
    }

    private static class AuctionRowMapper implements RowMapper<Auction> {
        @Override
        public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Auction auction = new Auction();
            auction.setAuctionId(rs.getInt("auction_id"));
            auction.setSeatId(rs.getInt("seat_id"));
            auction.setCurrentBid(rs.getDouble("current_bid"));

            int bidderId = rs.getInt("highest_bidder_id");
            if (!rs.wasNull()) {
                auction.setHighestBidderId(bidderId);
            }

            Timestamp ts = rs.getTimestamp("end_time");
            if (ts != null) {
                LocalDateTime end = ts.toLocalDateTime();
                auction.setEndTime(end);
                long seconds = Duration.between(LocalDateTime.now(), end).getSeconds();
                auction.setRemainingSeconds(Math.max(0, seconds));
            }

            auction.setStatus(rs.getString("status"));
            auction.setSeatNumber(rs.getString("seat_number"));
            auction.setMovieTitle(rs.getString("movie_title"));
            auction.setTheatreName(rs.getString("theatre_name"));
            auction.setHighestBidderName(rs.getString("bidder_name"));
            return auction;
        }
    }
}
