package ru.avg.server.mservice.meeting;

import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.MeetingUpdateDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;

public interface MeetingMService {

    MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto);

    MeetingDto update(Integer companyId, Integer meetingId, MeetingUpdateDto meetingDto);
}
