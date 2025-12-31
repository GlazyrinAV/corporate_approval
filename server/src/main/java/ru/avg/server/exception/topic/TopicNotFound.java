package ru.avg.server.exception.topic;

import lombok.Getter;

/**
 * Exception thrown when a topic with the specified ID is not found.
 * Provides structured access to the requested topic ID for logging and error handling.
 */
@Getter
public class TopicNotFound extends RuntimeException {

    private final Integer id;

    /**
     * Constructs a new TopicNotFound exception using the topic's ID.
     *
     * @param id the ID of the topic that was not found
     */
    public TopicNotFound(Integer id) {
        super("Topic with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}