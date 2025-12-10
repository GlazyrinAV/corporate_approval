package ru.avg.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipantDto {

    private Integer id;

    private Integer meetingId;

    private ParticipantDto participant;

    private Boolean isPresent = false;
}