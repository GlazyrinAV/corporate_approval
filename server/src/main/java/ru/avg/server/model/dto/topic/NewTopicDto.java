package ru.avg.server.model.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new topic in a meeting.
 * Used as input when creating a new topic, containing the minimal required information.
 * <p>
 * This DTO follows the standard pattern for creation requests, including validation
 * constraints to ensure data integrity before processing. The object is immutable after
 * construction and is typically converted to a domain entity by a mapper.
 * </p>
 *
 * @see TopicDto for the full topic representation including ID and other system-assigned fields
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewTopicDto {

    /**
     * The title of the topic being created.
     * Must not be null, empty, or contain only whitespace characters.
     * This represents the main subject or agenda item for discussion in a meeting.
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "Topic title must not be blank")
    private String title;

    /**
     * The ID of the meeting to which this topic belongs.
     * Must not be null, ensuring that every topic is associated with a valid meeting.
     * This field establishes the relationship between the topic and its parent meeting.
     *
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Meeting ID must not be null")
    private Integer meetingId;
}