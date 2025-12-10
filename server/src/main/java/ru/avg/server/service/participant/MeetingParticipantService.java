package ru.avg.server.service.participant;

import ru.avg.server.model.dto.MeetingParticipantDto;

import java.util.List;

public interface MeetingParticipantService {

    List<MeetingParticipantDto> save(List<MeetingParticipantDto> participants);

    List<MeetingParticipantDto> findAll(Integer meetingId);

    List<MeetingParticipantDto> findPotential(Integer meetingId);

    MeetingParticipantDto findByParticipantId(Integer meetingId, Integer participantId);

    void delete(Integer meetingParticipantId);
}