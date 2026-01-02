package ru.avg.server.exception.topic;

import lombok.Getter;

/**
 * Exception thrown when a topic with the specified ID is not found in the system.
 * <p>
 * This exception extends {@link RuntimeException} and is used throughout the application to indicate
 * that a requested topic resource does not exist. It provides access to the requested topic ID
 * through the getter method, enabling better error reporting, logging, and debugging capabilities.
 * </p>
 * <p>
 * The exception is typically thrown by service and repository layers when:
 * <ul>
 *   <li>A topic lookup by ID fails</li>
 *   <li>An operation requires a topic that doesn't exist</li>
 *   <li>A relationship references a non-existent topic</li>
 * </ul>
 * </p>
 * <p>
 * The exception message follows the format: "Topic with ID '{id}' not found."
 * where {id} is replaced with the actual topic ID that was requested but not found.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.topic.Topic
 * @author AVG
 * @since 1.0
 */
@Getter
public class TopicNotFound extends RuntimeException {

    /**
     * The identifier of the topic that could not be found in the system.
     * This field stores the requested topic ID to provide context about which resource
     * was not found, enabling better error reporting and debugging capabilities.
     *
     */
    private final Integer id;

    /**
     * Constructs a new TopicNotFound exception with the specified topic ID.
     * <p>
     * The constructor creates a descriptive error message that clearly indicates which
     * topic was not found by including the ID in the message. The message follows
     * the format: "Topic with ID '{id}' not found."
     * </p>
     *
     * @param id the ID of the topic that was requested but not found in the system; may be null
     * @throws NullPointerException if id is null (inherited from {@link RuntimeException} contract)
     * @see #getMessage()
     * @see #getId()
     */
    public TopicNotFound(Integer id) {
        super("Topic with ID '%d' not found.".formatted(id));
        this.id = id;
    }
}