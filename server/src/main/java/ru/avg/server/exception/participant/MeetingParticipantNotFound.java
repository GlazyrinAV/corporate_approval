package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when a meeting participant with the specified ID is not found.
 * Provides structured access to the requested participant ID for logging and error handling.
 */
@Getter
public class MeetingParticipantNotFound extends RuntimeException {

    private final Integer id;

    /**
     * Constructs a new MeetingParticipantNotFound exception with the specified participant ID.
     *
     * @param id the ID of the participant that was not found
     */
    public MeetingParticipantNotFound(Integer id) {
        super("Meeting participant with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}