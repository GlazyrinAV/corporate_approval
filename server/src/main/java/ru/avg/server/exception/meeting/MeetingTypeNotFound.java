package ru.avg.server.exception.meeting;

import lombok.Getter;

/**
 * Exception thrown when a meeting type with the specified title is not found.
 * Provides structured access to the requested type title for logging and error handling.
 */
@Getter
public class MeetingTypeNotFound extends RuntimeException {

    private final String type;

    /**
     * Constructs a new MeetingTypeNotFound exception with the specified meeting type.
     *
     * @param type the title of the meeting type that was not found
     */
    public MeetingTypeNotFound(String type) {
        super("Meeting type '%s' not found.".formatted(type));
        this.type = type;
    }
}