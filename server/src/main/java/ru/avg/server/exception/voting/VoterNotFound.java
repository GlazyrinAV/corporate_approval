package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when a voter is not found by name or ID.
 * Provides structured access to the lookup context (name or id) for logging and error handling.
 */
@Getter
public class VoterNotFound extends RuntimeException {

    private final String name;
    private final Integer id;

    /**
     * Constructs a new VoterNotFound exception using the voter's name.
     *
     * @param name the name of the voter that was not found
     */
    public VoterNotFound(String name) {
        super("Voter with name '%s' not found.".formatted(name));
        this.name = name;
        this.id = null;
    }

    /**
     * Constructs a new VoterNotFound exception using the voter's ID.
     *
     * @param id the ID of the voter that was not found
     */
    public VoterNotFound(Integer id) {
        super("Voter with ID '%d' not found.".formatted(id));
        this.id = id;
        this.name = null;
    }
}