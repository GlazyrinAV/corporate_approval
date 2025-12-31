package ru.avg.server.service.meeting;

import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.model.meeting.MeetingType;

import java.time.LocalDate;
import java.util.List;

public interface MeetingService {

    MeetingDto save(Integer companyId, MeetingDto meetingDto);

    MeetingDto update(Integer companyId, Integer meetingId, MeetingDto  newMeetingDto);

    void delete(Integer companyId, Integer meetingId);

    MeetingDto find(Integer companyId, MeetingType type, LocalDate date);

    MeetingDto findById(Integer companyId, Integer meetingId);

    List<MeetingDto> findAll(Integer companyId);
}