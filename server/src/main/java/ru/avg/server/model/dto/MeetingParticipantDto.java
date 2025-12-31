package ru.avg.server.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing a participant in a meeting.
 * Used to transfer meeting-participant association data between layers.
 *
 * Fields:
 * - id: Unique identifier for the meeting-participant link
 * - meetingId: Reference to the parent meeting (required)
 * - participant: Participant details (required)
 * - isPresent: Attendance status (defaults to false if not provided)
 */
@Data
@Builder
public class MeetingParticipantDto {

    private Integer id;

    @NotNull(message = "Meeting ID must not be null")
    private Integer meetingId;

    @Valid
    @NotNull(message = "Participant must not be null")
    private ParticipantDto participant;

    private Boolean isPresent = false;
}