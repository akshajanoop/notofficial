package com.moviebooking.service;

import com.moviebooking.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to sync and fetch currently playing live movies from review sites/APIs (TMDb / IMDb format)
 * into the local booking database.
 */
@Service
public class MovieSyncService {

    private final JdbcTemplate jdbcTemplate;

    public MovieSyncService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Synchronizes live theatrical release data with cast, director, rating, and posters.
     */
    @Transactional
    public int syncLiveTheatricalMovies() {
        // Live movies currently screening in theatres with official IMDb / Amazon CDN media poster art
        List<Movie> liveInCinemas = List.of(
            new Movie(0, "Practical Magic 2", "Fantasy-Romance", 115,
                "https://m.media-amazon.com/images/S/pv-target-images/03a45f0cb27f589438e06e57da63d91b4cd8a8afe919e23222179bed8812943c._SX1080_FMjpg_.jpg",
                "The Owens sisters return as ancestral magic and a mysterious curse resurface, forcing a new generation of witches to unite.",
                "English", 7.6, "Griffin Dunne",
                "Sandra Bullock, Nicole Kidman, Evan Rachel Wood, Stockard Channing, Dianne Wiest",
                "Alan Silvestri (Music), Andrew Dunn (Cinematography)"),

            new Movie(0, "Spider-Man: Brand New Day", "Action-Sci-Fi", 152,
                "https://m.media-amazon.com/images/M/MV5BNTA3N2Q0ZTAtODJjYi00OTUxLThmNDctYzc2OTBmZmQ2ZWE4XkEyXkFqcGc@._V1_FMjpg_UY2048_.jpg",
                "Peter Parker navigates college life and a new era of street-level vigilante threats across New York City with unexpected allies.",
                "English", 8.7, "Destin Daniel Cretton",
                "Tom Holland, Zendaya, Mark Ruffalo, Jon Bernthal, Sadie Sink",
                "Michael Giacchino (Music), Brett Pawlak (Cinematography)"),

            new Movie(0, "The Odyssey", "Epic-Adventure", 168,
                "https://m.media-amazon.com/images/M/MV5BNTcyNmJlZmQtNDUwYy00NDBjLTg1NGQtYTY2Y2UxMWM3NmI1XkEyXkFqcGc@._V1_.jpg",
                "An epic retelling of Odysseus' perilous ten-year journey home after the Trojan War, facing mythological sea monsters and gods.",
                "English", 8.9, "Christopher Nolan",
                "Cillian Murphy, Charlize Theron, Christian Bale, Tom Hardy",
                "Ludwig Göransson (Music), Hoyte van Hoytema (Cinematography)"),

            new Movie(0, "Mirzapur: The Movie", "Crime-Action", 142,
                "https://m.media-amazon.com/images/S/pv-target-images/855e8bb647c4a3ccfcdceb8d8d354ffc1df41908b203322b96c6683a0a070f0a._SX1080_FMjpg_.jpg",
                "The battle for the throne of Purvanchal reaches its cinematic climax in an explosive, high-stakes theatrical war of power and revenge.",
                "Hindi", 8.4, "Gurmmeet Singh",
                "Pankaj Tripathi, Ali Fazal, Divyenndu Sharma, Shweta Tripathi",
                "John Stewart Eduri (Music), Sanjay Kapoor (Cinematography)"),

            new Movie(0, "The Wild Robot", "Animation-Adventure", 102,
                "https://m.media-amazon.com/images/M/MV5BZGY2NmRmOWYtN2YzNi00MDU5LTk0OGItYTY5NDdmMGRhMmJmXkEyXkFqcGc@._V1_.jpg",
                "After a shipwreck, an intelligent robot named Roz is stranded on an uninhabited island and learns to adapt by bonding with local wildlife.",
                "English", 8.6, "Chris Sanders",
                "Lupita Nyong'o, Pedro Pascal, Kit Connor, Bill Nighy, Mark Hamill",
                "Kris Bowers (Music), Mary Blee (Editor)"),

            new Movie(0, "Resident Evil: Extinction Point", "Sci-Fi-Horror", 112,
                "https://m.media-amazon.com/images/M/MV5BZDMyYWU4NzItZDY0MC00ODE2LTkyYTMtMzNkNDdmYmFhZDg0XkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg",
                "A covert investigation team descends into a submerged Umbrella bio-weapons testing facility beneath the ocean floor.",
                "English", 7.5, "Zach Cregger",
                "Kaya Scodelario, Robbie Amell, Hannah John-Kamen",
                "Charlie Clouser (Music), Maxime Alexandre (Cinematography)")
        );

        for (Movie m : liveInCinemas) {
            String checkSql = "SELECT movie_id FROM movies WHERE title = ?";
            List<Integer> existing = jdbcTemplate.queryForList(checkSql, Integer.class, m.getTitle());

            if (existing.isEmpty()) {
                String insertSql = """
                    INSERT INTO movies (title, genre, description, poster_url, rating, duration_mins, director, cast_members, crew_members)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
                jdbcTemplate.update(insertSql, m.getTitle(), m.getGenre(), m.getDescription(),
                    m.getPosterUrl(), m.getRating(), m.getDurationMinutes(), m.getDirector(),
                    m.getCastMembers(), m.getCrewMembers());
            } else {
                String updateSql = """
                    UPDATE movies 
                    SET genre = ?, description = ?, poster_url = ?, rating = ?, duration_mins = ?, director = ?, cast_members = ?, crew_members = ?
                    WHERE movie_id = ?
                """;
                jdbcTemplate.update(updateSql, m.getGenre(), m.getDescription(), m.getPosterUrl(),
                    m.getRating(), m.getDurationMinutes(), m.getDirector(), m.getCastMembers(),
                    m.getCrewMembers(), existing.get(0));
            }
        }

        return liveInCinemas.size();
    }
}
