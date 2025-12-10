package ru.avg.server.service.participant;

import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.ParticipantType;

import java.util.List;

public interface ParticipantService {

    ParticipantDto save(ParticipantDto participantDto);

    ParticipantDto update(Integer participantId, ParticipantDto newParticipantDto);

    void delete(Integer participantId);

    ParticipantDto find(String name, Integer companyId, ParticipantType type);

    List<ParticipantDto> findAllByMeetingType(Integer companyId, MeetingType type);

    List<ParticipantDto> findAll(Integer companyId);

    ParticipantDto findById(Integer participantId);
}