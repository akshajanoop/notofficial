package com.moviebooking.repository;

import com.moviebooking.model.Theatre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Theatre locations, GPS coordinates, and geo-proximity queries.
 */
@Repository
public class TheatreRepository {

    private final JdbcTemplate jdbcTemplate;

    public TheatreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Theatre> findAll() {
        String sql = "SELECT * FROM theatres ORDER BY name";
        return jdbcTemplate.query(sql, new TheatreRowMapper());
    }

    public Optional<Theatre> findById(int theatreId) {
        String sql = "SELECT * FROM theatres WHERE theatre_id = ?";
        List<Theatre> theatres = jdbcTemplate.query(sql, new TheatreRowMapper(), theatreId);
        return theatres.isEmpty() ? Optional.empty() : Optional.of(theatres.get(0));
    }

    public List<Theatre> findByCity(String city) {
        String sql = "SELECT * FROM theatres WHERE LOWER(city) = LOWER(?) ORDER BY name";
        return jdbcTemplate.query(sql, new TheatreRowMapper(), city);
    }

    public int save(Theatre theatre) {
        String sql = "INSERT INTO theatres (name, location, city, latitude, longitude) VALUES (?, ?, ?, ?, ?)";
        org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, theatre.getName());
            ps.setString(2, theatre.getAddress());
            ps.setString(3, theatre.getCity());
            ps.setDouble(4, theatre.getLatitude() != 0.0 ? theatre.getLatitude() : 19.0760);
            ps.setDouble(5, theatre.getLongitude() != 0.0 ? theatre.getLongitude() : 72.8777);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            theatre.setTheatreId(key.intValue());
            return key.intValue();
        }
        return 0;
    }

    public boolean delete(int theatreId) {
        String sql = "DELETE FROM theatres WHERE theatre_id = ?";
        return jdbcTemplate.update(sql, theatreId) > 0;
    }

    private static class TheatreRowMapper implements RowMapper<Theatre> {
        @Override
        public Theatre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Theatre theatre = new Theatre();
            theatre.setTheatreId(rs.getInt("theatre_id"));
            theatre.setName(rs.getString("name"));
            theatre.setAddress(rs.getString("location"));
            theatre.setCity(rs.getString("city"));
            theatre.setLatitude(rs.getDouble("latitude"));
            theatre.setLongitude(rs.getDouble("longitude"));
            return theatre;
        }
    }
}
