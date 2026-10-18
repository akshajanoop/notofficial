package com.moviebooking.service;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Theatre;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.TheatreRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Intelligent Recommendation Engine providing:
 * 1. "For You" - Personalized movie recommendations based on user genre booking history.
 * 2. "Near You" - GPS Location-Based Services (Haversine formula) calculating real-time distance in kilometers.
 */
@Service
public class RecommendationService {

    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final BookingRepository bookingRepository;

    public RecommendationService(MovieRepository movieRepository,
                                  TheatreRepository theatreRepository,
                                  BookingRepository bookingRepository) {
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Recommends movies matching genres previously booked by the user.
     */
    public List<Movie> getForYouRecommendations(Integer userId) {
        if (userId == null) {
            return movieRepository.findTopRated(4);
        }

        List<String> preferredGenres = bookingRepository.findGenresBookedByUser(userId);
        if (preferredGenres.isEmpty()) {
            return movieRepository.findTopRated(4);
        }

        Set<Integer> addedIds = new HashSet<>();
        List<Movie> recommendations = new ArrayList<>();

        for (String genre : preferredGenres) {
            List<Movie> genreMovies = movieRepository.findByGenre(genre);
            for (Movie m : genreMovies) {
                if (addedIds.add(m.getMovieId())) {
                    recommendations.add(m);
                }
            }
        }

        if (recommendations.isEmpty()) {
            return movieRepository.findTopRated(4);
        }

        return recommendations;
    }

    /**
     * Finds theatres near the user using exact GPS coordinates (Latitude & Longitude)
     * and sorts them in ascending order of proximity using the Haversine Distance Formula.
     */
    public List<Theatre> getTheatresNearGps(double userLat, double userLng) {
        List<Theatre> all = theatreRepository.findAll();
        for (Theatre t : all) {
            double distance = calculateHaversineDistanceKm(userLat, userLng, t.getLatitude(), t.getLongitude());
            t.setDistanceKm(Math.round(distance * 10.0) / 10.0); // round to 1 decimal place
        }
        all.sort(Comparator.comparing(Theatre::getDistanceKm));
        return all;
    }

    /**
     * Fallback city-based lookup with estimated distance if GPS is not enabled.
     */
    public List<Theatre> getNearYouTheatres(String userCity) {
        if (userCity == null || userCity.trim().isEmpty()) {
            return theatreRepository.findAll();
        }
        List<Theatre> cityTheatres = theatreRepository.findByCity(userCity);
        return cityTheatres.isEmpty() ? theatreRepository.findAll() : cityTheatres;
    }

    /**
     * Haversine formula to compute great-circle distance between two GPS coordinates in Kilometers.
     */
    private double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
