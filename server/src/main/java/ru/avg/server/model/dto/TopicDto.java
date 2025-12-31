package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Topic entity.
 * Used for creating and updating topics via API.
 *
 * Fields:
 * - id: Optional, assigned by the system on creation
 * - title: Required, subject or agenda item of the topic
 * - meetingId: Required, reference to the parent meeting
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {

    private Integer id;

    @NotBlank(message = "Topic title must not be blank")
    private String title;

    @NotNull(message = "Meeting ID must not be null")
    private Integer meetingId;
}