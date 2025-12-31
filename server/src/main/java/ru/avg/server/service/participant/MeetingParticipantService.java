package ru.avg.server.service.participant;

import ru.avg.server.model.dto.MeetingParticipantDto;

import java.util.List;

public interface MeetingParticipantService {

    List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants);

    List<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId);

    List<MeetingParticipantDto> findPotential(Integer companyId, Integer meetingId);

    MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId);

    void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId);
}