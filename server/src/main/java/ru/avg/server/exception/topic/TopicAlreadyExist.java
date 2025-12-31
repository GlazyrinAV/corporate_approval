package ru.avg.server.exception.topic;

/**
 * Exception thrown when attempting to create a topic that already exists.
 * This exception does not carry additional context (e.g., topic ID or title),
 * and is intended for simple cases where only a generic conflict message is needed.
 */
public class TopicAlreadyExist extends RuntimeException {

    /**
     * Constructs a new TopicAlreadyExist exception with a default English message.
     * Using English ensures consistency in logs and backend systems.
     * Localization should be handled at the API layer if needed.
     */
    public TopicAlreadyExist() {
        super("The topic already exists.");
    }
}