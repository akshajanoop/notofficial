package com.moviebooking.controller;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Show & Theatre Selection screen.
 * Shows available timings grouped by theatre.
 */
@Controller
public class MovieController {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public MovieController(MovieRepository movieRepository, ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    @GetMapping("/movies/{movieId}/shows")
    public String showTimings(@PathVariable("movieId") int movieId, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            return "redirect:/";
        }

        Movie movie = movieOpt.get();
        List<Show> shows = showRepository.findByMovieId(movieId);

        // Group shows by Theatre name for structured presentation
        Map<String, List<Show>> showsByTheatre = shows.stream()
                .collect(Collectors.groupingBy(Show::getTheatreName));

        model.addAttribute("movie", movie);
        model.addAttribute("showsByTheatre", showsByTheatre);
        model.addAttribute("totalShows", shows.size());

        return "shows";
    }
}
