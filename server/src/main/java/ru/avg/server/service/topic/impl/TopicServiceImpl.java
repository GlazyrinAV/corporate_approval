package ru.avg.server.service.topic.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.model.dto.mapper.TopicMapper;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.service.topic.TopicService;
import ru.avg.server.service.voting.VotingService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final VotingService votingService;

    private final TopicMapper topicMapper;

    @Override
    public TopicDto save(TopicDto topicDto) {
        Topic newTopic = topicRepository.save(topicMapper.fromDto(topicDto));
        votingService.create(newTopic);
        return topicMapper.toDto(newTopic);
    }

    @Override
    public TopicDto edit(Integer topicId, TopicDto topicDto) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId));
        if (!topicDto.getTitle().isBlank()) {
            topic.setTitle(topicDto.getTitle());
        }
        return topicMapper.toDto(topicRepository.save(topic));
    }

    @Override
    public void delete(Integer topicId) {
        topicRepository.deleteById(topicId);
    }

    @Override
    public List<TopicDto> findAllByMeeting_Id(Integer meetingId) {
        return topicRepository.findAllByMeeting_Id(meetingId).stream()
                .map(topicMapper::toDto)
                .toList();
    }

    @Override
    public TopicDto findById(Integer topicId) {
        return topicMapper.toDto(topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId)));
    }
}