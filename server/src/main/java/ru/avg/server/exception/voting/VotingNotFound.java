package ru.avg.server.exception.voting;

import lombok.Getter;

/**
 * Exception thrown when a voting with the specified ID is not found.
 * Provides structured access to the requested voting ID for logging and error handling.
 */
@Getter
public class VotingNotFound extends RuntimeException {

    private final Integer id;

    /**
     * Constructs a new VotingNotFound exception using the voting's ID.
     *
     * @param id the ID of the voting that was not found
     */
    public VotingNotFound(Integer id) {
        super("Voting with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}