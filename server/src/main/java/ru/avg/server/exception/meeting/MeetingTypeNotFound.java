package ru.avg.server.exception.meeting;

import lombok.Getter;

/**
 * Exception thrown when attempting to retrieve or process a meeting type that does not exist in the system.
 * This exception is typically used when a meeting type lookup by its title fails, indicating that the
 * requested meeting type (e.g., "Совет директоров", "Общее собрание участников") is not among the supported
 * types defined in {@link ru.avg.server.model.meeting.MeetingType}.
 * <p>
 * The exception carries the requested meeting type title to facilitate error handling, logging,
 * and user-friendly error messages that can specify exactly which meeting type was not found.
 * The error message is formatted in English as "Meeting type '{type}' not found." where {type}
 * is replaced with the actual meeting type title that was searched for.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.meeting.MeetingType
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingTypeNotFound extends RuntimeException {

    /**
     * The title of the meeting type that could not be found in the system.
     * This field stores the requested meeting type title (in Russian) that was used for lookup
     * but did not match any existing meeting type in the system. The title is preserved to
     * enable contextual error reporting and debugging.
     *
     */
    private final String type;

    /**
     * Constructs a new MeetingTypeNotFound exception with the specified meeting type title.
     * Initializes the exception with a descriptive error message in English that includes
     * the meeting type title, and stores the title for later retrieval through the getter method.
     *
     * @param type the title of the meeting type that was requested but not found in the system; must not be null
     * @throws NullPointerException if type is null
     * @see #getMessage()
     * @see #getType()
     */
    public MeetingTypeNotFound(String type) {
        super("Meeting type '%s' not found.".formatted(type));
        this.type = type;
    }
}