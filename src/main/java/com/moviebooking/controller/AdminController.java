package com.moviebooking.controller;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import com.moviebooking.model.Theatre;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowRepository;
import com.moviebooking.repository.TheatreRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller providing Administrative operations for CineReserve:
 * - Adding & Deleting Movies (with IMDb details & poster URL)
 * - Adding & Deleting Theatres (with GPS latitude & longitude)
 * - Scheduling new Shows (with automated 30-seat grid creation)
 * Secured: Only accessible when logged in with administrator credentials.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public AdminController(MovieRepository movieRepository,
                           TheatreRepository theatreRepository,
                           ShowRepository showRepository,
                           SeatRepository seatRepository) {
        this.movieRepository   = movieRepository;
        this.theatreRepository = theatreRepository;
        this.showRepository    = showRepository;
        this.seatRepository    = seatRepository;
    }

    private boolean checkNotAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return isAdmin == null || !isAdmin;
    }

    @GetMapping
    public String adminDashboard(HttpSession session, @RequestParam(required = false) String msg, Model model) {
        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }

        List<Movie> movies     = movieRepository.findAll();
        List<Theatre> theatres = theatreRepository.findAll();
        List<Show> shows       = showRepository.findAll();

        model.addAttribute("movies", movies);
        model.addAttribute("theatres", theatres);
        model.addAttribute("shows", shows);
        model.addAttribute("msg", msg);
        model.addAttribute("today", LocalDate.now().toString());

        return "admin";
    }

    // --- MOVIE OPERATIONS ---

    @PostMapping("/movies/add")
    public String addMovie(HttpSession session,
                           @RequestParam String title,
                           @RequestParam(required = false, defaultValue = "Action") String genre,
                           @RequestParam(required = false, defaultValue = "") String description,
                           @RequestParam(required = false) String posterUrl,
                           @RequestParam(defaultValue = "7.5") double rating,
                           @RequestParam(defaultValue = "120") int durationMinutes,
                           @RequestParam(required = false, defaultValue = "Renowned Director") String director,
                           @RequestParam(required = false, defaultValue = "Lead Actor, Supporting Cast") String castMembers) {

        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }

        if (posterUrl == null || posterUrl.trim().isEmpty()) {
            posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80";
        }

        Movie movie = new Movie();
        movie.setTitle(title.trim());
        movie.setGenre(genre.trim());
        movie.setDescription(description.trim());
        movie.setPosterUrl(posterUrl.trim());
        movie.setRating(rating);
        movie.setDurationMinutes(durationMinutes);
        movie.setDirector(director.trim());
        movie.setCastMembers(castMembers.trim());
        movie.setCrewMembers(director.trim() + " (Director)");

        movieRepository.save(movie);

        return "redirect:/admin?msg=movie_added";
    }

    @PostMapping("/movies/delete")
    public String deleteMovie(HttpSession session, @RequestParam int movieId) {
        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }
        movieRepository.delete(movieId);
        return "redirect:/admin?msg=movie_deleted";
    }

    // --- THEATRE OPERATIONS ---

    @PostMapping("/theatres/add")
    public String addTheatre(HttpSession session,
                             @RequestParam String name,
                             @RequestParam String city,
                             @RequestParam String address,
                             @RequestParam(defaultValue = "19.0760") double latitude,
                             @RequestParam(defaultValue = "72.8777") double longitude) {

        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }

        Theatre theatre = new Theatre();
        theatre.setName(name.trim());
        theatre.setCity(city.trim());
        theatre.setAddress(address.trim());
        theatre.setLatitude(latitude);
        theatre.setLongitude(longitude);

        theatreRepository.save(theatre);

        return "redirect:/admin?msg=theatre_added";
    }

    @PostMapping("/theatres/delete")
    public String deleteTheatre(HttpSession session, @RequestParam int theatreId) {
        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }
        theatreRepository.delete(theatreId);
        return "redirect:/admin?msg=theatre_deleted";
    }

    // --- SHOW OPERATIONS ---

    @PostMapping("/shows/add")
    public String addShow(HttpSession session,
                          @RequestParam int movieId,
                          @RequestParam int theatreId,
                          @RequestParam String showDate,
                          @RequestParam String showTime) {

        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }

        Show show = new Show();
        show.setMovieId(movieId);
        show.setTheatreId(theatreId);
        LocalDate date = LocalDate.parse(showDate);
        LocalTime time = LocalTime.parse(showTime);
        show.setShowTime(java.time.LocalDateTime.of(date, time));

        int showId = showRepository.save(show);
        if (showId > 0) {
            // Automatically initialize the 30-seat grid with tiers (Standard, Premium, Recliner)
            seatRepository.populateDefaultSeatsIfEmpty(showId);
        }

        return "redirect:/admin?msg=show_added";
    }

    @PostMapping("/shows/delete")
    public String deleteShow(HttpSession session, @RequestParam int showId) {
        if (checkNotAdmin(session)) {
            return "redirect:/login?error=admin_required";
        }
        showRepository.delete(showId);
        return "redirect:/admin?msg=show_deleted";
    }
}
