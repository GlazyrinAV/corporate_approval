package ru.avg.server.model.dto.topic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.topic.NewTopicDto;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.meeting.MeetingRepository;

/**
 * Mapper component responsible for converting {@link NewTopicDto} to {@link Topic} entity.
 * This class handles the transformation of topic data from the API layer (DTO) to the domain
 * model layer (entity), including resolving the meeting relationship by ID.
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives its
 * dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * </p>
 *
 * @author AVG
 * @see NewTopicDto
 * @see Topic
 * @see MeetingRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NewTopicMapper {

    /**
     * Repository used to fetch meeting entities by ID when mapping topic DTOs.
     * This dependency is injected by Spring and used to establish the relationship
     * between a topic and its parent meeting during the mapping process.
     * <p>
     * The repository is used to validate that the meeting ID provided in the DTO
     * corresponds to an existing meeting, throwing {@link MeetingNotFound} if not found.
     * </p>
     */
    private final MeetingRepository meetingRepository;

    /**
     * Converts a {@link NewTopicDto} object into a {@link Topic} entity.
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
    public Topic fromDto(NewTopicDto topicDto) {
        if (topicDto == null) {
            throw new IllegalArgumentException("NewTopicDto must not be null");
        }
        if (topicDto.getMeetingId() == null) {
            throw new IllegalArgumentException("Meeting ID must not be null");
        }

        return Topic.builder()
                .title(topicDto.getTitle())
                .meeting(meetingRepository.findById(topicDto.getMeetingId())
                        .orElseThrow(() -> new MeetingNotFound(topicDto.getMeetingId())))
                .build();
    }
}