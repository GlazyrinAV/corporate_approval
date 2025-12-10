package ru.avg.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoterDto {

    private Integer id;

    private Integer votingId;

    private MeetingParticipantDto participant;

    private Integer topicId;

    private String vote;

    private boolean isRelatedPartyDeal = false;
}