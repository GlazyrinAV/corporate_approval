package ru.avg.server.model.dto.topic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.topic.TopicDto;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.meeting.MeetingRepository;

/**
 * Mapper component responsible for bidirectional conversion between {@link Topic} entities
 * and {@link TopicDto} data transfer objects.
 * <p>
 * This class provides methods to transform data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of the topic's attributes and its
 * relationship with the parent meeting. The mapper validates referential integrity by
 * ensuring the meeting ID in the DTO corresponds to an existing meeting during the
 * {@code fromDto} operation.
 * </p>
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * </p>
 *
 * @author AVG
 * @see Topic
 * @see TopicDto
 * @see MeetingRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class TopicMapper {

    /**
     * Repository used to fetch meeting entities by ID when mapping topic DTOs.
     * This dependency is injected by Spring and used to establish the relationship
     * between a topic and its parent meeting during the mapping process.
     * <p>
     * The repository is used to validate that the meeting ID provided in the DTO
     * corresponds to an existing meeting, throwing {@link MeetingNotFound} if not found.
     * This ensures referential integrity at the mapping layer.
     * </p>
     */
    private final MeetingRepository meetingRepository;

    /**
     * Converts a {@link TopicDto} object into a {@link Topic} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code meetingId} from the DTO into a proper {@link ru.avg.server.model.meeting.Meeting} entity
     * by fetching it from the repository. The resulting Topic entity is built with all
     * required relationships established.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input DTO is not null</li>
     *   <li>The meeting ID in the DTO is not null</li>
     *   <li>The specified meeting exists in the database</li>
     * </ul>
     * </p>
     *
     * @param topicDto the DTO containing topic data, must not be {@code null} and must have a non-null meetingId
     * @return a fully constructed {@link Topic} entity with mapped values and resolved meeting relationship
     * @throws IllegalArgumentException if {@code topicDto} is {@code null} or if {@code meetingId} is null
     * @throws MeetingNotFound          if the {@code meetingId} in the DTO does not correspond to any existing meeting
     * @see Topic#builder()
     * @see MeetingRepository#findById(Object)
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
     * Converts a {@link Topic} entity into a {@link TopicDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * extracting the meeting ID from the associated meeting entity and including it in the DTO.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input entity is not null</li>
     *   <li>The topic is associated with a meeting (meeting reference is not null)</li>
     * </ul>
     * </p>
     *
     * @param topic the entity containing topic data, must not be {@code null}
     *              and must have an associated meeting
     * @return a fully constructed {@link TopicDto} with mapped values
     * @throws IllegalArgumentException if {@code topic} is {@code null}
     *                                  or if it has no associated meeting
     * @see TopicDto#builder()
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