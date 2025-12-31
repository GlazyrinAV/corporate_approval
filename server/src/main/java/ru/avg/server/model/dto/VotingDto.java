package ru.avg.server.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for Voting entity.
 * Used to represent voting results in API responses.
 *
 * Fields:
 * - id: Unique identifier of the voting session
 * - topicId: Reference to the topic being voted on
 * - isAccepted: Final outcome of the vote (true = accepted, false = rejected)
 * - votersId: List of participant IDs who took part in the voting
 *
 * Note: This DTO is for read-only responses. Use {@link VotingCreationDto} for creating votings.
 */
@Data
@Builder
public class VotingDto {

    private Integer id;

    private Integer topicId;

    private boolean isAccepted;

    private List<Integer> votersId;
}