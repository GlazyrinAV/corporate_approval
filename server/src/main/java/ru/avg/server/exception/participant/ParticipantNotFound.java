package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when a participant cannot be found in the system, either by name or by ID.
 * This exception extends {@link RuntimeException} and is used throughout the application to indicate
 * that a requested participant resource does not exist. It provides structured access to the search
 * context through getter methods, allowing callers to determine which lookup method was used and
 * what parameters were involved.
 * <p>
 * Depending on the constructor used, either the participant name or the participant ID will be populated,
 * while the other field will be null. This design allows the exception to carry context about how
 * the lookup was performed, which is useful for logging, debugging, and generating appropriate
 * error responses in API endpoints.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.participant.Participant
 * @author AVG
 * @since 1.0
 */
@Getter
public class ParticipantNotFound extends RuntimeException {

    /**
     * The name of the participant that could not be found in the system.
     * This field is populated when the exception is thrown due to a failed lookup by participant name.
     * It will be null when the exception is constructed with an ID.
     *
     */
    private final String name;

    /**
     * The identifier of the participant that could not be found in the system.
     * This field is populated when the exception is thrown due to a failed lookup by participant ID.
     * It will be null when the exception is constructed with a name.
     *
     */
    private final Integer id;

    /**
     * Constructs a new ParticipantNotFound exception for a participant identified by their name.
     * <p>
     * This constructor is used when a participant lookup by name fails. The exception message
     * clearly indicates that no participant with the specified name exists in the system.
     * The {@link #name} field is populated with the requested name, while {@link #id} is set to null
     * to indicate that the lookup was performed by name only.
     * </p>
     *
     * @param name the name of the participant that was requested but not found in the system; must not be null
     * @throws NullPointerException if name is null
     * @see #getName()
     * @see #getMessage()
     */
    public ParticipantNotFound(String name) {
        super("Participant with name '%s' not found.".formatted(name));
        this.name = name;
        this.id = null;
    }

    /**
     * Constructs a new ParticipantNotFound exception for a participant identified by their ID.
     * <p>
     * This constructor is used when a participant lookup by ID fails. The exception message
     * clearly indicates that no participant with the specified ID exists in the system.
     * The {@link #id} field is populated with the requested ID, while {@link #name} is set to null
     * to indicate that the lookup was performed by ID only.
     * </p>
     *
     * @param id the ID of the participant that was requested but not found in the system; may be null
     * @see #getId()
     * @see #getMessage()
     */
    public ParticipantNotFound(Integer id) {
        super("Participant with ID '%d' not found.".formatted(id));
        this.id = id;
        this.name = null;
    }
}