package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when a voter cannot be found in the system, either by name or by ID.
 * <p>
 * This exception extends {@link RuntimeException} and is used throughout the application to indicate
 * that a requested voter resource does not exist. It provides structured access to the search
 * context through getter methods, allowing callers to determine which lookup method was used and
 * what parameters were involved.
 * </p>
 * <p>
 * Depending on the constructor used, either the voter name or the voter ID will be populated,
 * while the other field will be null. This design allows the exception to carry context about how
 * the lookup was performed, which is useful for logging, debugging, and generating appropriate
 * error responses in API endpoints.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.voting.Voter
 * @author AVG
 * @since 1.0
 */
@Getter
public class VoterNotFound extends RuntimeException {

    /**
     * The name of the voter that could not be found in the system.
     * This field is populated when the exception is thrown due to a failed lookup by voter name.
     * It will be null when the exception is constructed with an ID.
     *
     */
    private final String name;

    /**
     * The identifier of the voter that could not be found in the system.
     * This field is populated when the exception is thrown due to a failed lookup by voter ID.
     * It will be null when the exception is constructed with a name.
     *
     */
    private final Integer id;

    /**
     * Constructs a new VoterNotFound exception for a voter identified by their ID.
     * <p>
     * This constructor is used when a voter lookup by ID fails. The exception message
     * clearly indicates that no voter with the specified ID exists in the system.
     * The {@link #id} field is populated with the requested ID, while {@link #name} is set to null
     * to indicate that the lookup was performed by ID only.
     * </p>
     *
     * @param id the ID of the voter that was requested but not found in the system; may be null
     * @see #getId()
     * @see #getMessage()
     */
    public VoterNotFound(Integer id) {
        super("Voter with ID '%d' not found.".formatted(id));
        this.id = id;
        this.name = null;
    }
}