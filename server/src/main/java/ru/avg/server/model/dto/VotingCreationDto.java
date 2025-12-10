package ru.avg.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VotingCreationDto {

    private List<VoterDto> voters = new ArrayList<>();

    public void addVoter(VoterDto voter) {
        voters.add(voter);
    }
}