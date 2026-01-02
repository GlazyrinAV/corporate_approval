package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when a voting session with the specified ID is not found in the system.
 * <p>
 * This exception extends {@link RuntimeException} and is used throughout the application to indicate
 * that a requested voting resource does not exist. It provides access to the requested voting ID
 * through the getter method, enabling better error reporting, logging, and debugging capabilities.
 * </p>
 * <p>
 * The exception is typically thrown by service and repository layers when:
 * <ul>
 *   <li>A voting lookup by ID fails</li>
 *   <li>An operation requires a voting that doesn't exist</li>
 *   <li>A relationship references a non-existent voting session</li>
 * </ul>
 * </p>
 * <p>
 * The exception message follows the format: "Voting with ID '{id}' not found."
 * where {id} is replaced with the actual voting ID that was requested but not found.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.voting.Voting
 * @author AVG
 * @since 1.0
 */
@Getter
public class VotingNotFound extends RuntimeException {

    /**
     * The identifier of the voting session that could not be found in the system.
     * This field stores the requested voting ID to provide context about which resource
     * was not found, enabling better error reporting and debugging capabilities.
     *
     */
    private final Integer id;

    /**
     * Constructs a new VotingNotFound exception with the specified voting ID.
     * <p>
     * The constructor creates a descriptive error message that clearly indicates which
     * voting session was not found by including the ID in the message. The message follows
     * the format: "Voting with ID '{id}' not found."
     * </p>
     *
     * @param id the ID of the voting session that was requested but not found in the system; may be null
     * @throws NullPointerException if id is null (inherited from {@link RuntimeException} contract)
     * @see #getMessage()
     * @see #getId()
     */
    public VotingNotFound(Integer id) {
        super("Voting with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}