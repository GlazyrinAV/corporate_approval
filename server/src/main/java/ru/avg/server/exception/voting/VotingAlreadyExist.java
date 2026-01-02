package ru.avg.server.exception.voting;

/**
 * Exception thrown when attempting to create a voting session for a topic that already has an existing voting session.
 * <p>
 * This exception is used by services and repositories to enforce business rules that prevent multiple voting sessions
 * on the same agenda topic, ensuring data consistency and proper governance process flow. The exception uses a
 * standardized English message for consistency in logs, monitoring systems, and API responses, with localization
 * expected to be handled at the controller advice level if needed for client-facing messages.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.voting.Voting
 * @author AVG
 * @since 1.0
 */
public class VotingAlreadyExist extends RuntimeException {

    /**
     * Constructs a new VotingAlreadyExist exception with a descriptive English message.
     * <p>
     * The message "Voting has already been created for this topic." clearly indicates that a voting session
     * already exists for the specified topic, preventing duplicate voting processes. Using English ensures
     * consistency across the application's internal logging and error tracking systems. Any localization
     * for end-user messages should be handled at the API layer by controllers or exception handlers.
     * </p>
     *
     * @see #getMessage()
     */
    public VotingAlreadyExist() {
        super("Voting has already been created for this topic.");
    }
}