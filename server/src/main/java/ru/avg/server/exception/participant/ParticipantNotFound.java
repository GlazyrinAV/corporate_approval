package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when a participant is not found by name or ID.
 * Provides structured access to the lookup context (name or id) for logging and error handling.
 */
@Getter
public class ParticipantNotFound extends RuntimeException {

    private final String name;
    private final Integer id;

    /**
     * Constructs a new ParticipantNotFound exception using the participant's name.
     *
     * @param name the name of the participant that was not found
     */
    public ParticipantNotFound(String name) {
        super("Participant with name '%s' not found.".formatted(name));
        this.name = name;
        this.id = null;
    }

    /**
     * Constructs a new ParticipantNotFound exception using the participant's ID.
     *
     * @param id the ID of the participant that was not found
     */
    public ParticipantNotFound(Integer id) {
        super("Participant with ID '%d' not found.".formatted(id));
        this.id = id;
        this.name = null;
    }
}