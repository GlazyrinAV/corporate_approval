package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when attempting to retrieve or process a participant type that does not exist in the system.
 * This exception is typically used when a participant type lookup by its title fails, indicating that the
 * requested participant type (e.g., "Участник", "Акционер", "Директор") is not among the supported types defined
 * in the application's participant type enumeration.
 * <p>
 * The exception carries the requested participant type title to facilitate error handling, logging,
 * and user-friendly error messages that can specify exactly which participant type was not found.
 * The error message is formatted in English as "Participant type '{type}' not found." where {type}
 * is replaced with the actual participant type title that was searched for.
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class ParticipantTypeNotFound extends RuntimeException {

    /**
     * The title of the participant type that could not be found in the system.
     * This field stores the requested participant type title (in Russian) that was used for lookup
     * but did not match any existing participant type in the system. The title is preserved to
     * enable contextual error reporting and debugging.
     *
     */
    private final String type;

    /**
     * Constructs a new ParticipantTypeNotFound exception with the specified participant type title.
     * Initializes the exception with a descriptive error message in English that includes
     * the participant type title, and stores the title for later retrieval through the getter method.
     *
     * @param type the title of the participant type that was requested but not found in the system; must not be null
     * @throws NullPointerException if type is null
     * @see #getMessage()
     * @see #getType()
     */
    public ParticipantTypeNotFound(String type) {
        super("Participant type '%s' not found.".formatted(type));
        this.type = type;
    }
}