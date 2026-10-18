package com.moviebooking.controller;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Theatre;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.service.MovieSyncService;
import com.moviebooking.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for Home Screen (Movie listings, genre filters, and GPS location-aware theatre recommendations).
 */
@Controller
public class HomeController {

    private final MovieRepository movieRepository;
    private final RecommendationService recommendationService;
    private final MovieSyncService movieSyncService;

    public HomeController(MovieRepository movieRepository, 
                          RecommendationService recommendationService,
                          MovieSyncService movieSyncService) {
        this.movieRepository = movieRepository;
        this.recommendationService = recommendationService;
        this.movieSyncService = movieSyncService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        String userCity = (String) session.getAttribute("userCity");

        List<Movie> allMovies = movieRepository.findAll();
        List<String> genres = movieRepository.findAllGenres();

        // Personalized "For You" Recommendations
        List<Movie> forYou = recommendationService.getForYouRecommendations(userId);

        // Default Theatres by City Proximity
        List<Theatre> nearYouTheatres = recommendationService.getNearYouTheatres(userCity);

        model.addAttribute("movies", allMovies);
        model.addAttribute("genres", genres);
        model.addAttribute("forYou", forYou);
        model.addAttribute("theatres", nearYouTheatres);

        return "home";
    }

    /**
     * AJAX endpoint to calculate nearest theatres with real-time GPS distance in Kilometers.
     */
    @GetMapping("/api/theatres/nearby")
    @ResponseBody
    public List<Theatre> getNearbyTheatres(@RequestParam("lat") double lat, @RequestParam("lng") double lng) {
        return recommendationService.getTheatresNearGps(lat, lng);
    }

    /**
     * API endpoint to trigger a real-time sync with movie review & box-office data.
     */
    @PostMapping("/api/movies/sync")
    @ResponseBody
    public Map<String, Object> syncMovies() {
        int synced = movieSyncService.syncLiveTheatricalMovies();
        return Collections.singletonMap("syncedCount", synced);
    }
}
