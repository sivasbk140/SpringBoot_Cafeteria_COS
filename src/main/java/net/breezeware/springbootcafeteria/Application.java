package net.breezeware.springbootcafeteria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the SpringBoot Cafeteria application.
 * <p>
 * This class bootstraps the Spring Boot application context and starts
 * the embedded web server for the cafeteria ordering system.
 * </p>
 */
@SpringBootApplication
public class Application {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }

}
