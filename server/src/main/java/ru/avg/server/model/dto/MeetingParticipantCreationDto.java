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
public class MeetingParticipantCreationDto {

    private List<MeetingParticipantDto> potentialParticipants = new ArrayList<>();

    public void addParticipant(MeetingParticipantDto participant) {
        potentialParticipants.add(participant);
    }

}