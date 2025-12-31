package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.meeting.MeetingRepository;

/**
 * Mapper for converting between {@link Topic} entities and {@link TopicDto} data transfer objects.
 * This class is responsible for bidirectional mapping while ensuring referential integrity,
 * particularly by validating the existence of the associated meeting during the mapping process.
 * It is designed as a Spring component to be autowired into services that require topic-related conversions.
 */
@Component
@RequiredArgsConstructor
public class TopicMapper {

    private final MeetingRepository meetingRepository;

    /**
     * Converts a {@link TopicDto} into a persistent {@link Topic} entity.
     * Resolves the associated meeting by its ID from the DTO using the {@link MeetingRepository}.
     * Ensures that the provided DTO and meeting ID are not null, throwing appropriate exceptions otherwise.
     *
     * @param topicDto the data transfer object to convert; must not be null
     * @return a fully constructed {@link Topic} entity with all fields mapped
     * @throws IllegalArgumentException if the provided {@code topicDto} or its {@code meetingId} is null
     * @throws MeetingNotFound          if no meeting exists in the database with the specified meeting ID
     */
    public Topic fromDto(TopicDto topicDto) {
        if (topicDto == null) {
            throw new IllegalArgumentException("TopicDto must not be null");
        }
        if (topicDto.getMeetingId() == null) {
            throw new IllegalArgumentException("Meeting ID must not be null");
        }

        return Topic.builder()
                .id(topicDto.getId())
                .title(topicDto.getTitle())
                .meeting(meetingRepository.findById(topicDto.getMeetingId())
                        .orElseThrow(() -> new MeetingNotFound(topicDto.getMeetingId())))
                .build();
    }

    /**
     * Converts a persistent {@link Topic} entity into a {@link TopicDto} for external use.
     * Extracts the associated meeting's ID and includes it in the resulting DTO.
     * Validates that both the topic entity and its meeting reference are not null.
     *
     * @param topic the entity to convert; must not be null
     * @return a fully populated {@link TopicDto} with all relevant fields, including the meeting ID
     * @throws IllegalArgumentException if the provided {@code topic} is null or if it has no associated meeting
     */
    public TopicDto toDto(Topic topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Topic must not be null");
        }
        if (topic.getMeeting() == null) {
            throw new IllegalArgumentException("Topic must be associated with a meeting");
        }

        return TopicDto.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .meetingId(topic.getMeeting().getId())
                .build();
    }
}