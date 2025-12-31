package ru.avg.server.service.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.model.dto.mapper.MeetingMapper;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.meeting.MeetingRepository;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.service.meeting.MeetingService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    private final CompanyRepository companyRepository;

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final MeetingMapper meetingMapper;

    @Override
    public MeetingDto save(Integer companyId, MeetingDto meetingDto) {
        checkCompanyId(companyId);
        return meetingMapper.toDto(meetingRepository.save(meetingMapper.fromDto(meetingDto)));
    }

    @Override
    public MeetingDto update(Integer companyId, Integer meetingId, MeetingDto newMeetingDto) {
        checkCompanyId(companyId);
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFound(meetingId));
        Meeting newMeeting = meetingMapper.fromDto(newMeetingDto);

        if (newMeeting.getType() != null && newMeeting.getType() != meeting.getType()) {
            meeting.setType(newMeeting.getType());
            meetingParticipantRepository.deleteAll(meetingParticipantRepository.findAllByMeetingId(meetingId));
        }
        if (newMeeting.getDate() != null) {
            meeting.setDate(newMeeting.getDate());
        }
        if (!newMeeting.getAddress().isBlank()) {
            meeting.setAddress(newMeeting.getAddress());
        }
        if (newMeeting.getSecretary() != null) {
            meeting.setSecretary(newMeeting.getSecretary());
        }
        if (newMeeting.getChairman() != null) {
            meeting.setChairman(newMeeting.getChairman());
        }
        return meetingMapper.toDto(meetingRepository.save(meeting));
    }

    @Override
    public void delete(Integer companyId, Integer meetingId) {
        checkCompanyId(companyId);
        meetingRepository.deleteById(meetingId);
    }

    @Override
    public MeetingDto find(Integer companyId, MeetingType type, LocalDate date) {
        checkCompanyId(companyId);
        return meetingMapper.toDto(meetingRepository.findByCompanyIdAndTypeAndDate(companyId, type, date)
                .orElseThrow(() -> new MeetingNotFound(companyId, date)));
    }

    @Override
    public MeetingDto findById(Integer companyId, Integer meetingId) {
        checkCompanyId(companyId);
        return meetingMapper.toDto(meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFound(meetingId)));
    }

    @Override
    public List<MeetingDto> findAll(Integer companyId) {
        checkCompanyId(companyId);
        List<Meeting> allMeetings = meetingRepository.findAllByCompanyId(companyId);
        return allMeetings.stream()
                .map(meetingMapper::toDto)
                .toList();
    }

    private void checkCompanyId(Integer companyId) {
        if (companyRepository.findById(companyId).isEmpty()) {
            throw new CompanyNotFound(companyId);
        }
    }
}