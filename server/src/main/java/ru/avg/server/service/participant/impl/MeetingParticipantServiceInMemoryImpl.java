package ru.avg.server.service.participant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.MeetingParticipantNotFound;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.model.dto.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.mapper.TopicMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.meeting.MeetingRepository;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.service.topic.TopicService;
import ru.avg.server.service.voting.VotingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingParticipantServiceInMemoryImpl implements MeetingParticipantService {

    private final MeetingParticipantMapper meetingParticipantMapper;

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final CompanyRepository  companyRepository;

    private final MeetingRepository  meetingRepository;

    private final TopicService topicService;

    private final TopicMapper topicMapper;

    private final VotingService votingService;

    private final ParticipantService participantService;

    private final MeetingService meetingService;

    @Override
    public List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        for (MeetingParticipantDto participant : participants) {
            MeetingParticipant meetingParticipant = meetingParticipantRepository.save(meetingParticipantMapper.fromDto(participant));
            List<TopicDto> topics = topicService.findAllByMeetingId(companyId, meetingParticipant.getMeeting().getId());
            if (topics != null) {
                for (TopicDto topic : topics) {
                    votingService.create(topicMapper.fromDto(topic));
                }
            }
        }
        return participants;
    }

    @Override
    public List<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        return meetingParticipantRepository.findAllByMeetingId(meetingId)
                .stream()
                .filter(x -> x.getMeeting().getId().equals(meetingId))
                .map(meetingParticipantMapper::toDto)
                .toList();
    }

    @Override
    public List<MeetingParticipantDto> findPotential(Integer companyId, Integer meetingId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        MeetingDto meetingDto = meetingService.findById(companyId, meetingId);
        List<MeetingParticipantDto> currentParticipants = meetingParticipantRepository.findAllByMeetingId(meetingId).stream()
                .map(meetingParticipantMapper::toDto)
                .toList();
        List<MeetingParticipantDto> potentialParticipants = participantService.findAllByMeetingType(meetingDto.getCompanyId(),
                        Arrays.stream(MeetingType.values())
                                .filter(x -> x.getTitle().equals(meetingDto.getType()))
                                .findFirst()
                                .orElseThrow(() -> new MeetingTypeNotFound(meetingDto.getType())))
                .stream()
                .filter(ParticipantDto::getIsActive)
                .map(meetingParticipantMapper::fromParticipantDto)
                .toList();
        List<MeetingParticipantDto> result = new ArrayList<>();
        for (MeetingParticipantDto participant : potentialParticipants) {
            if (currentParticipants.stream().noneMatch(x -> x.getParticipant().getId().equals(participant.getParticipant().getId()))) {
                participant.setMeetingId(meetingId);
                result.add(participant);
            }
        }
        return result;
    }

    @Override
    public MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        return meetingParticipantMapper.toDto(meetingParticipantRepository.findByMeetingIdAndParticipantId(meetingId, participantId)
                .orElseThrow(() -> new MeetingParticipantNotFound(participantId)));
    }

    @Override
    public void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        meetingParticipantRepository.deleteById(meetingParticipantId);
    }

    private void checkCompanyIdAndMeetingId(Integer companyId, Integer meetingId) {
        if (companyRepository.findById(companyId).isEmpty()) {
            throw new CompanyNotFound(companyId);
        }

        if (meetingRepository.findById(meetingId).isEmpty()) {
            throw new MeetingNotFound(meetingId);
        }
    }
}