package ru.avg.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Corporate Approval System.
 * <p>
 * This class serves as the entry point for the Spring Boot application and configures
 * the overall application context. It enables component scanning, auto-configuration,
 * and other Spring Boot features through the {@link SpringBootApplication} annotation.
 * </p>
 * <p>
 * The application provides a comprehensive API for managing corporate governance processes
 * including meetings, participants, topics, and voting procedures. The OpenAPI definition
 * provides metadata about the API for documentation and client generation purposes.
 * </p>
 *
 * @see SpringBootApplication
 * @see OpenAPIDefinition
 * @see Info
 * @author AVG
 * @since 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Corporate Approval API",
                version = "1.0",
                description = "API for managing corporate approvals, meetings, topics, participants, and voting"
        )
)
@SpringBootApplication
public class ServerApplication {

    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * <p>
     * This method launches the embedded web server, initializes the Spring application context,
     * and starts processing requests. It delegates to {@link SpringApplication#run(Class, String[])}
     * to bootstrap the application, register the configuration class, and start the application.
     * </p>
     *
     * @param args command-line arguments passed to the application
     * @see SpringApplication#run(Class, String[])
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}