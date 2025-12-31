package ru.avg.server.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for adding participants to a meeting.
 * Contains a list of MeetingParticipantDto objects to be added.
 * Used in POST requests to associate participants with a meeting.
 */
@Data
@Builder
public class MeetingParticipantCreationDto {

    @Valid
    @NotNull(message = "List of potential participants must not be null")
    private List<@Valid MeetingParticipantDto> potentialParticipants = new ArrayList<>();

    /**
     * Adds a participant to the list of potential participants.
     *
     * @param participant the MeetingParticipantDto to add
     * @throws IllegalArgumentException if participant is null
     */
    public void addParticipant(MeetingParticipantDto participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null");
        }
        potentialParticipants.add(participant);
    }
}