package com.example.buswise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RE:ENGINEER - Intelligent Bus Transport Management System (BusWise)
 *
 * Main entry point for the Spring Boot application.
 * Boots embedded web server (Tomcat on port 8080), JPA Hibernate repositories,
 * REST controllers, and serves the responsive front-end dashboard.
 */
@SpringBootApplication
public class BusWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusWiseApplication.class, args);
    }
}
