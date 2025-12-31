package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when a participant type with the specified title is not found.
 * Provides structured access to the requested type title for logging and error handling.
 */
@Getter
public class ParticipantTypeNotFound extends RuntimeException {

    private final String type;

    /**
     * Constructs a new ParticipantTypeNotFound exception with the specified participant type.
     *
     * @param type the title of the participant type that was not found
     */
    public ParticipantTypeNotFound(String type) {
        super("Participant type '%s' not found.".formatted(type));
        this.type = type;
    }
}