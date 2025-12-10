package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.meeting.MeetingRepository;

@Component
@AllArgsConstructor
public class TopicMapper {

    private MeetingRepository meetingRepository;

    public Topic fromDto(TopicDto topicDto) {
        return Topic.builder()
                .id(topicDto.getId())
                .title(topicDto.getTitle())
                .meeting(meetingRepository.findById(topicDto.getMeetingId())
                        .orElseThrow(() -> new MeetingNotFound(topicDto.getMeetingId())))
                .build();
    }

    public TopicDto toDto(Topic topic) {
        return TopicDto.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .meetingId(topic.getMeeting().getId())
                .build();
    }
}