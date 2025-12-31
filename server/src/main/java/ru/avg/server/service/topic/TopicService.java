package ru.avg.server.service.topic;

import ru.avg.server.model.dto.TopicDto;

import java.util.List;

public interface TopicService {

    TopicDto save(Integer companyId, Integer meetingId, TopicDto topicDto);

    TopicDto edit(Integer companyId, Integer meetingId, Integer topicId, TopicDto topicDto);

    void delete(Integer companyId, Integer meetingId, Integer topicId);

    List<TopicDto> findAllByMeetingId(Integer companyId, Integer meetingId);

    TopicDto findById(Integer companyId, Integer meetingId, Integer topicId);
}