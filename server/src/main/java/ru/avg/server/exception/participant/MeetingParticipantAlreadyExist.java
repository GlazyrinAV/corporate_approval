package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when attempting to add a participant to a meeting who is already a participant.
 * Provides structured access to the participant ID for logging and error handling purposes.
 */
@Getter
public class MeetingParticipantAlreadyExist extends RuntimeException {

    private final String id;

    /**
     * Constructs a new MeetingParticipantAlreadyExist exception with the specified participant ID.
     *
     * @param id the ID of the participant that already exists in the meeting
     */
    public MeetingParticipantAlreadyExist(String id) {
        super("MeetingParticipant with id '%s' already exists.".formatted(id));
        this.id = id;
    }
}