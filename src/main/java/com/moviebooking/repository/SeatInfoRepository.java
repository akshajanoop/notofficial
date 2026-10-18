package com.moviebooking.repository;

import com.moviebooking.model.Seat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatInfoRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Seat> findAllSeatsWithShows() {
        String sql = """
            SELECT st.*, s.show_id, m.title AS movie_title, t.name AS theatre_name
            FROM seats st
            JOIN shows s ON st.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE st.status = 'AVAILABLE'
            ORDER BY st.seat_id ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Seat seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setShowId(rs.getInt("show_id"));
            seat.setSeatNumber(rs.getString("seat_number"));
            seat.setPrice(rs.getDouble("price"));
            return seat;
        });
    }
}
