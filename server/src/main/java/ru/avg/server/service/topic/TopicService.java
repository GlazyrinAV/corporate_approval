package ru.avg.server.service.topic;

import ru.avg.server.model.dto.TopicDto;

import java.util.List;

public interface TopicService {

    TopicDto save(TopicDto topicDto);

    TopicDto edit(Integer topicId, TopicDto topicDto);

    void delete(Integer topicId);

    List<TopicDto> findAllByMeeting_Id(Integer meetingId);

    TopicDto findById(Integer topicId);
}