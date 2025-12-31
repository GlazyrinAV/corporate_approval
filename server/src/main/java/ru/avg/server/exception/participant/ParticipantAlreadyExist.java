package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a participant that already exists.
 * Provides structured access to the participant's name for logging and error handling.
 */
@Getter
public class ParticipantAlreadyExist extends RuntimeException {

    private final String name;

    /**
     * Constructs a new ParticipantAlreadyExist exception with the specified participant name.
     *
     * @param name the name of the participant that already exists
     */
    public ParticipantAlreadyExist(String name) {
        super("Participant with name '%s' already exists.".formatted(name));
        this.name = name;
    }
}