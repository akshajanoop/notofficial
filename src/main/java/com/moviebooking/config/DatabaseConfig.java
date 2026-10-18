package com.moviebooking.config;

import org.springframework.context.annotation.Configuration;

/**
 * Database & Spring MVC Configuration.
 * 
 * Note: JdbcTemplate and DataSource connection pools (HikariCP) are auto-configured
 * by Spring Boot using application.properties parameters.
 * 
 * Works seamlessly with:
 * 1. Embedded in-memory H2 (default mode for zero-setup local runs).
 * 2. Standalone MySQL instance via standard JDBC drivers.
 */
@Configuration
public class DatabaseConfig {
    // Configuration beans can be added here as the application scales.
}
