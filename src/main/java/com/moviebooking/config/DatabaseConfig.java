package com.moviebooking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Database & Data Persistence Configuration.
 * 
 * Configured with persistent on-disk database storage. When the application
 * launches, it checks if movies already exist in the database:
 * - If empty (first launch): Seeds sample movies, theatres, shows, and users from data.sql.
 * - If non-empty (restarts/recompilations): Preserves all user & admin-created entries permanently.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public CommandLineRunner initDatabaseIfEmpty(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Check if movies table already contains records
                Integer movieCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movies", Integer.class);
                if (movieCount == null || movieCount == 0) {
                    System.out.println("===> [CineReserve DB] Clean database detected. Seeding initial catalog from seed-data.sql...");
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("seed-data.sql"));
                    populator.execute(dataSource);
                    System.out.println("===> [CineReserve DB] Initial seed data successfully populated.");
                } else {
                    System.out.println("===> [CineReserve DB] Persistent database active (" + movieCount + " movies found). All admin entries and edits preserved.");
                }
            } catch (Exception e) {
                System.err.println("===> [CineReserve DB] Error verifying database initialization state: " + e.getMessage());
            }
        };
    }
}
