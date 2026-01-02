package ru.avg.server.model.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a topic in a meeting.
 * This class is used to transfer topic data between layers of the application,
 * particularly between the controller and service layers during API requests.
 * <p>
 * The TopicDto serves as both an input DTO for creating/updating topics and an output DTO
 * for returning topic information to clients. It contains all the essential fields needed
 * to represent a topic, including its identifier, title, and association with a meeting.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, while {@link NoArgsConstructor} and
 * {@link AllArgsConstructor} ensure compatibility with serialization frameworks and
 * dependency injection containers.
 * </p>
 *
 * @see NewTopicDto for the creation-specific variant that excludes the ID field
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {

    /**
     * The unique identifier of the topic.
     * This field is optional and is typically null when creating a new topic,
     * as the ID is assigned by the system upon persistence. After creation,
     * this field contains the generated ID that can be used for subsequent
     * operations such as updates or deletions.
     */
    private Integer id;

    /**
     * The title or subject of the topic.
     * This represents the main agenda item or discussion point in a meeting.
     * The title must not be null, empty, or contain only whitespace characters.
     * It serves as the primary descriptor of what the topic is about and is
     * displayed to users in meeting agendas and summaries.
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "Topic title must not be blank")
    private String title;

    /**
     * The identifier of the meeting to which this topic belongs.
     * This field establishes the relationship between the topic and its parent meeting,
     * ensuring that every topic is associated with a valid meeting context.
     * The meetingId must not be null, enforcing referential integrity at the DTO level.
     *
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Meeting ID must not be null")
    private Integer meetingId;
}