package ru.avg.server.model.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewMeetingParticipantDto {

    private Integer participantId;

    private Boolean isPresent = false;
}
