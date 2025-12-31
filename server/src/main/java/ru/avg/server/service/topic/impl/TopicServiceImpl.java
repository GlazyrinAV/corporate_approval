package ru.avg.server.service.topic.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.model.dto.mapper.TopicMapper;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.meeting.MeetingRepository;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.service.topic.TopicService;
import ru.avg.server.service.voting.VotingService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final CompanyRepository companyRepository;

    private final MeetingRepository meetingRepository;

    private final VotingService votingService;

    private final TopicMapper topicMapper;

    @Override
    public TopicDto save(Integer companyId, Integer meetingId, TopicDto topicDto) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        Topic newTopic = topicRepository.save(topicMapper.fromDto(topicDto));
        votingService.create(newTopic);
        return topicMapper.toDto(newTopic);
    }

    @Override
    public TopicDto edit(Integer companyId, Integer meetingId, Integer topicId, TopicDto topicDto) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId));
        if (!topicDto.getTitle().isBlank()) {
            topic.setTitle(topicDto.getTitle());
        }
        return topicMapper.toDto(topicRepository.save(topic));
    }

    @Override
    public void delete(Integer companyId, Integer meetingId, Integer topicId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        topicRepository.deleteById(topicId);
    }

    @Override
    public List<TopicDto> findAllByMeetingId(Integer companyId, Integer meetingId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        return topicRepository.findAllByMeeting_Id(meetingId).stream()
                .map(topicMapper::toDto)
                .toList();
    }

    @Override
    public TopicDto findById(Integer companyId, Integer meetingId, Integer topicId) {
        checkCompanyIdAndMeetingId(companyId, meetingId);
        return topicMapper.toDto(topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId)));
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