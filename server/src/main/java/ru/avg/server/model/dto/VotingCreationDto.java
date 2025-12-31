package ru.avg.server.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating a voting session.
 * Contains a list of voters participating in the vote.
 * Used in POST requests to initiate voting on a topic.
 */
@Data
@Builder
public class VotingCreationDto {

    @Valid
    @NotNull(message = "List of voters must not be null")
    private List<@Valid VoterDto> voters = new ArrayList<>();

    /**
     * Adds a voter to the voting session.
     *
     * @param voter the VoterDto to add
     * @throws IllegalArgumentException if voter is null
     */
    public void addVoter(@Valid VoterDto voter) {
        if (voter == null) {
            throw new IllegalArgumentException("Voter cannot be null");
        }
        voters.add(voter);
    }
}