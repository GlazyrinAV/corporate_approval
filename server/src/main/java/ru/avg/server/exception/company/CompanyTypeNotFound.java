package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when a company type with the specified title is not found.
 * This exception provides access to the requested title for logging and error handling purposes.
 */
@Getter
public class CompanyTypeNotFound extends RuntimeException {

    private final String title;

    /**
     * Constructs a new CompanyTypeNotFound exception with the specified company type title.
     *
     * @param title the title of the company type that was not found
     */
    public CompanyTypeNotFound(String title) {
        super("Company type '%s' not found.".formatted(title));
        this.title = title;
    }
}