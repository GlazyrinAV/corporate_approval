package ru.avg.server.exception.voting;

/**
 * Exception thrown when attempting to create a voting for a topic that already has one.
 * This exception does not carry additional context but uses a standardized English message
 * for consistency across the application layer.
 */
public class VotingAlreadyExist extends RuntimeException {

    /**
     * Constructs a new VotingAlreadyExist exception with a clear English message.
     * Using English ensures consistency in logs, monitoring, and international teams.
     * Localization should be handled at the API layer (e.g., via @ControllerAdvice) if needed.
     */
    public VotingAlreadyExist() {
        super("Voting has already been created for this topic.");
    }
}