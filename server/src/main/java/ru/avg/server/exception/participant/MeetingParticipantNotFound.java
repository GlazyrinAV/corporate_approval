package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when a meeting participant with the specified ID is not found in the system.
 * This exception extends {@link RuntimeException} and is used throughout the application to indicate
 * that a requested meeting participant resource does not exist. It provides access to the requested
 * participant ID through the getter method, enabling better error reporting, logging, and debugging.
 * <p>
 * The exception is typically thrown by service and repository layers when:
 * <ul>
 *   <li>A meeting participant lookup by ID fails</li>
 *   <li>An operation requires a meeting participant that doesn't exist</li>
 *   <li>A relationship references a non-existent meeting participant</li>
 * </ul>
 * </p>
 * <p>
 * The exception message follows the format: "Meeting participant with ID '{id}' not found."
 * where {id} is replaced with the actual participant ID that was requested but not found.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.participant.MeetingParticipant
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingParticipantNotFound extends RuntimeException {

    /**
     * The identifier of the meeting participant that could not be found in the system.
     * This field stores the requested participant ID to provide context about which resource
     * was not found, enabling better error reporting and debugging capabilities.
     *
     */
    private final Integer id;

    /**
     * Constructs a new MeetingParticipantNotFound exception with the specified participant ID.
     * <p>
     * The constructor creates a descriptive error message that clearly indicates which
     * meeting participant was not found by including the ID in the message. The message
     * follows the format: "Meeting participant with ID '{id}' not found."
     * </p>
     *
     * @param id the ID of the meeting participant that was requested but not found in the system; may be null
     * @throws NullPointerException if id is null (inherited from {@link RuntimeException} contract)
     * @see #getMessage()
     * @see #getId()
     */
    public MeetingParticipantNotFound(Integer id) {
        super("Meeting participant with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}