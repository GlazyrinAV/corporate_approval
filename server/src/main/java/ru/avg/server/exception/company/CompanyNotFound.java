package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when a company with the specified ID is not found.
 * This exception provides access to the requested company ID for logging and error handling purposes.
 */
@Getter
public class CompanyNotFound extends RuntimeException {

    private final Integer id;

    /**
     * Constructs a new CompanyNotFound exception with the specified company ID.
     *
     * @param id the ID of the company that was not found
     */
    public CompanyNotFound(Integer id) {
        super("Company with ID " + id + " not found.");
        this.id = id;
    }
}