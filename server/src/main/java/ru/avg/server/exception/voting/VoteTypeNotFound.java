package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when a vote type with the specified title is not found.
 * Provides structured access to the requested type title for logging and error handling.
 */
@Getter
public class VoteTypeNotFound extends RuntimeException {

    private final String type;

    /**
     * Constructs a new VoteTypeNotFound exception with the specified vote type.
     *
     * @param type the title of the vote type that was not found
     */
    public VoteTypeNotFound(String type) {
        super("Vote type '%s' not found.".formatted(type));
        this.type = type;
    }
}