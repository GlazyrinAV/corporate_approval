package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when a company with the specified ID is not found in the system.
 * This exception extends {@link RuntimeException} and is typically used in service and repository layers
 * to indicate that a requested company resource does not exist. It provides access to the requested company ID
 * to facilitate error handling, logging, and user-friendly error messages.
 * <p>
 * The exception includes a descriptive error message in English that incorporates the company ID,
 * making it easy to identify which company was not found during debugging and monitoring.
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class CompanyNotFound extends RuntimeException {

    /**
     * The identifier of the company that could not be found in the system.
     * This field stores the requested company ID to provide context about which resource
     * was not found, enabling better error reporting and debugging capabilities.
     *
     */
    private final Integer id;

    /**
     * Constructs a new CompanyNotFound exception with the specified company ID.
     * Initializes the exception with a descriptive error message that includes the company ID,
     * and stores the ID for later retrieval through the getter method.
     *
     * @param id the ID of the company that was requested but not found in the system; may be null
     * @see #getMessage()
     * @see #getId()
     */
    public CompanyNotFound(Integer id) {
        super("Company with ID " + id + " not found.");
        this.id = id;
    }
}