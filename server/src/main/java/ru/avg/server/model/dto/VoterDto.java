package ru.avg.server.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Voter entity.
 * Used to submit voting data for a participant in a meeting topic.
 *
 * Fields:
 * - id: Optional, assigned by the system on creation
 * - votingId: Required, reference to the parent voting session
 * - participant: Required, the meeting participant casting the vote
 * - topicId: Required, reference to the topic being voted on
 * - vote: Required, must match a valid VoteType (e.g., "YES", "NO")
 * - isRelatedPartyDeal: Optional, indicates if the vote involves a related-party transaction (defaults to false)
 */
@Data
@Builder
public class VoterDto {

    private Integer id;

    @NotNull(message = "Voting ID must not be null")
    private Integer votingId;

    @Valid
    @NotNull(message = "Participant must not be null")
    private MeetingParticipantDto participant;

    @NotNull(message = "Topic ID must not be null")
    private Integer topicId;

    @NotNull(message = "Vote type must not be null")
    private String vote;

    private boolean isRelatedPartyDeal = false;
}