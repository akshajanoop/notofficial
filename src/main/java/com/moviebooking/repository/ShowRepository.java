package com.moviebooking.repository;

import com.moviebooking.model.Show;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Shows (screenings) linking Movies, Theatres, and Showtime schedules.
 */
@Repository
public class ShowRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Show> findByMovieId(int movieId) {
        String sql = """
            SELECT s.*, m.title AS movie_title, t.name AS theatre_name,
                   (SELECT COUNT(*) FROM seats st WHERE st.show_id = s.show_id AND st.status = 'AVAILABLE') AS available_seats
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE s.movie_id = ?
            ORDER BY s.show_date, s.show_time
        """;
        return jdbcTemplate.query(sql, new ShowRowMapper(), movieId);
    }

    public Optional<Show> findById(int showId) {
        String sql = """
            SELECT s.*, m.title AS movie_title, t.name AS theatre_name,
                   (SELECT COUNT(*) FROM seats st WHERE st.show_id = s.show_id AND st.status = 'AVAILABLE') AS available_seats
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            WHERE s.show_id = ?
        """;
        List<Show> shows = jdbcTemplate.query(sql, new ShowRowMapper(), showId);
        return shows.isEmpty() ? Optional.empty() : Optional.of(shows.get(0));
    }

    public List<Show> findAll() {
        String sql = """
            SELECT s.*, m.title AS movie_title, t.name AS theatre_name,
                   (SELECT COUNT(*) FROM seats st WHERE st.show_id = s.show_id AND st.status = 'AVAILABLE') AS available_seats
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN theatres t ON s.theatre_id = t.theatre_id
            ORDER BY s.show_date DESC, s.show_time DESC
        """;
        return jdbcTemplate.query(sql, new ShowRowMapper());
    }

    public int save(Show show) {
        String sql = "INSERT INTO shows (movie_id, theatre_id, show_time, show_date, total_seats) VALUES (?, ?, ?, ?, ?)";
        org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, show.getMovieId());
            ps.setInt(2, show.getTheatreId());
            ps.setTime(3, java.sql.Time.valueOf(show.getShowTime().toLocalTime()));
            ps.setDate(4, java.sql.Date.valueOf(show.getShowTime().toLocalDate()));
            ps.setInt(5, 30);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            show.setShowId(key.intValue());
            return key.intValue();
        }
        return 0;
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean delete(int showId) {
        // 1. Find all seats belonging to this show
        List<Integer> seatIds = jdbcTemplate.queryForList("SELECT seat_id FROM seats WHERE show_id = ?", Integer.class, showId);
        if (!seatIds.isEmpty()) {
            String inSql = String.join(",", seatIds.stream().map(String::valueOf).toList());
            jdbcTemplate.update("DELETE FROM auctions WHERE seat_id IN (" + inSql + ")");
            jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id IN (" + inSql + ")");
        }
        // 2. Delete bookings for this show
        List<Integer> bookingIds = jdbcTemplate.queryForList("SELECT booking_id FROM bookings WHERE show_id = ?", Integer.class, showId);
        for (Integer bookingId : bookingIds) {
            jdbcTemplate.update("DELETE FROM booking_seats WHERE booking_id = ?", bookingId);
        }
        jdbcTemplate.update("DELETE FROM bookings WHERE show_id = ?", showId);
        // 3. Delete seats
        jdbcTemplate.update("DELETE FROM seats WHERE show_id = ?", showId);
        // 4. Delete show
        String sql = "DELETE FROM shows WHERE show_id = ?";
        return jdbcTemplate.update(sql, showId) > 0;
    }

    public boolean update(Show show) {
        String sql = "UPDATE shows SET movie_id = ?, theatre_id = ?, show_time = ?, show_date = ? WHERE show_id = ?";
        return jdbcTemplate.update(sql,
                show.getMovieId(),
                show.getTheatreId(),
                java.sql.Time.valueOf(show.getShowTime().toLocalTime()),
                java.sql.Date.valueOf(show.getShowTime().toLocalDate()),
                show.getShowId()) > 0;
    }

    private static class ShowRowMapper implements RowMapper<Show> {
        @Override
        public Show mapRow(ResultSet rs, int rowNum) throws SQLException {
            Show show = new Show();
            show.setShowId(rs.getInt("show_id"));
            show.setMovieId(rs.getInt("movie_id"));
            show.setTheatreId(rs.getInt("theatre_id"));

            LocalDate date = rs.getDate("show_date").toLocalDate();
            LocalTime time = rs.getTime("show_time").toLocalTime();
            show.setShowTime(LocalDateTime.of(date, time));

            show.setMovieTitle(rs.getString("movie_title"));
            show.setTheatreName(rs.getString("theatre_name"));
            show.setAvailableSeatCount(rs.getInt("available_seats"));
            return show;
        }
    }
}
