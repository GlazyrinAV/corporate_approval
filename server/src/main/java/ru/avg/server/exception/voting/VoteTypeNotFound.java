package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when attempting to retrieve or process a vote type that does not exist in the system.
 * This exception is typically used when a vote type lookup by its title fails, indicating that the
 * requested vote type (e.g., "YES", "NO", "ABSTAINED") is not among the supported types defined
 * in {@link ru.avg.server.model.voting.VoteType}. The exception carries the requested type title
 * to facilitate error handling, logging, and user-friendly error messages.
 * <p>
 * The exception message follows the format: "Vote type '{type}' not found." where {type}
 * is replaced with the actual vote type title that was searched for.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.voting.VoteType
 * @author AVG
 * @since 1.0
 */
@Getter
public class VoteTypeNotFound extends RuntimeException {

    /**
     * The title of the vote type that could not be found in the system.
     * This field stores the requested vote type title that was used for lookup
     * but did not match any existing vote type in the system. The title is preserved to
     * enable contextual error reporting and debugging.
     *
     */
    private final String type;

    /**
     * Constructs a new VoteTypeNotFound exception with the specified vote type title.
     * <p>
     * The constructor creates a descriptive error message in English that includes
     * the vote type title, and stores the title for later retrieval through the getter method.
     * This allows services and controllers to provide meaningful feedback when an invalid
     * or unsupported vote type is requested.
     * </p>
     *
     * @param type the title of the vote type that was requested but not found in the system; must not be null
     * @throws NullPointerException if type is null
     * @see #getMessage()
     * @see #getType()
     */
    public VoteTypeNotFound(String type) {
        super("Vote type '%s' not found.".formatted(type));
        this.type = type;
    }
}