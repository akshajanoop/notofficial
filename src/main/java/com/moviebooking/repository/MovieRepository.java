package com.moviebooking.repository;

import com.moviebooking.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC Repository for Movie catalog operations including Cast & Crew details.
 */
@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Movie> findAll() {
        String sql = "SELECT * FROM movies ORDER BY rating DESC";
        return jdbcTemplate.query(sql, new MovieRowMapper());
    }

    public Optional<Movie> findById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        List<Movie> movies = jdbcTemplate.query(sql, new MovieRowMapper(), movieId);
        return movies.isEmpty() ? Optional.empty() : Optional.of(movies.get(0));
    }

    public List<Movie> findByGenre(String genre) {
        String sql = "SELECT * FROM movies WHERE LOWER(genre) = LOWER(?) ORDER BY rating DESC";
        return jdbcTemplate.query(sql, new MovieRowMapper(), genre);
    }

    public List<String> findAllGenres() {
        String sql = "SELECT DISTINCT genre FROM movies WHERE genre IS NOT NULL ORDER BY genre";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public int save(Movie movie) {
        String sql = "INSERT INTO movies (title, genre, description, poster_url, rating, duration_mins, director, cast_members, crew_members) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setString(3, movie.getDescription());
            ps.setString(4, movie.getPosterUrl());
            ps.setDouble(5, movie.getRating());
            ps.setInt(6, movie.getDurationMinutes());
            ps.setString(7, movie.getDirector());
            ps.setString(8, movie.getCastMembers());
            ps.setString(9, movie.getCrewMembers());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            movie.setMovieId(key.intValue());
            return key.intValue();
        }
        return 0;
    }

    public boolean delete(int movieId) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        return jdbcTemplate.update(sql, movieId) > 0;
    }

    public List<Movie> findTopRated(int limit) {
        String sql = "SELECT * FROM movies ORDER BY rating DESC LIMIT ?";
        return jdbcTemplate.query(sql, new MovieRowMapper(), limit);
    }

    private static class MovieRowMapper implements RowMapper<Movie> {
        @Override
        public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
            Movie movie = new Movie();
            movie.setMovieId(rs.getInt("movie_id"));
            movie.setTitle(rs.getString("title"));
            movie.setGenre(rs.getString("genre"));
            movie.setDescription(rs.getString("description"));
            movie.setPosterUrl(rs.getString("poster_url"));
            movie.setRating(rs.getDouble("rating"));
            movie.setDurationMinutes(rs.getInt("duration_mins"));
            movie.setDirector(rs.getString("director"));
            movie.setCastMembers(rs.getString("cast_members"));
            movie.setCrewMembers(rs.getString("crew_members"));
            return movie;
        }
    }
}
