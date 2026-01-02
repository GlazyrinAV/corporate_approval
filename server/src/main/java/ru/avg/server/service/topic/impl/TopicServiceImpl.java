package ru.avg.server.service.topic.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.model.dto.topic.NewTopicDto;
import ru.avg.server.model.dto.topic.TopicDto;
import ru.avg.server.model.dto.topic.mapper.NewTopicMapper;
import ru.avg.server.model.dto.topic.mapper.TopicMapper;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.service.topic.TopicService;
import ru.avg.server.service.voting.VotingService;
import ru.avg.server.utils.updater.Updater;
import ru.avg.server.utils.verifier.Verifier;

import java.util.List;

/**
 * Implementation of {@link TopicService} providing business logic for managing meeting topics.
 * This service handles the complete lifecycle of topics including creation, retrieval,
 * update, and deletion with proper transaction management and business rule enforcement.
 * <p>
 * The class uses constructor injection via {@link RequiredArgsConstructor} to receive
 * all its dependencies, promoting immutability and testability. It delegates to repositories
 * for data access, mappers for DTO conversion, and utility services for validation and updates.
 * </p>
 *
 * @author AVG
 * @see TopicService
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final VotingService votingService;
    private final TopicMapper topicMapper;
    private final NewTopicMapper newTopicMapper;
    private final Verifier verifier;
    private final Updater updater;

    /**
     * Creates a new topic for a specific meeting and associates it with a voting process.
     * This operation is transactional to ensure data consistency between topic creation
     * and voting initialization.
     *
     * @param companyId the ID of the company (for access control and verification)
     * @param meetingId the ID of the meeting for which the topic is created
     * @param topicDto  the DTO containing topic data to be persisted
     * @return the created TopicDto with system-assigned fields (ID)
     * @throws IllegalArgumentException                        if topicDto is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if company doesn't exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if meeting doesn't exist
     * @see NewTopicMapper#fromDto(NewTopicDto)
     * @see VotingService#save(Topic)
     */
    @Override
    @Transactional
    public TopicDto save(Integer companyId, Integer meetingId, NewTopicDto topicDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        Topic newTopic = topicRepository.save(newTopicMapper.fromDto(topicDto));
        votingService.save(newTopic);

        return topicMapper.toDto(newTopic);
    }

    /**
     * Updates an existing topic with new information.
     * Only non-null fields from the update DTO are applied to the existing entity.
     * This operation is transactional to ensure atomic updates.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting (for access control)
     * @param topicId   the ID of the topic to update
     * @param topicDto  the DTO containing fields to update
     * @return the updated TopicDto reflecting changes in persistence
     * @throws TopicNotFound            if topic with given ID doesn't exist
     * @throws IllegalArgumentException if topicDto is null
     */
    @Override
    @Transactional
    public TopicDto update(Integer companyId, Integer meetingId, Integer topicId, NewTopicDto topicDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        Topic currentTopic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId));

        Topic sourceTopic = newTopicMapper.fromDto(topicDto);
        Topic updatedTopic = updater.update(currentTopic, sourceTopic);

        return topicMapper.toDto(topicRepository.save(updatedTopic));
    }

    /**
     * Deletes a topic by its ID.
     * This operation is transactional to ensure complete removal of the topic
     * and any related data, though currently only removes the topic record.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting (for access control)
     * @param topicId   the ID of the topic to delete
     * @throws TopicNotFound if topic with given ID doesn't exist
     */
    @Override
    @Transactional
    public void delete(Integer companyId, Integer meetingId, Integer topicId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);
        topicRepository.deleteById(topicId);
    }

    /**
     * Retrieves all topics associated with a specific meeting.
     * This is a read-only operation that returns topics in their natural order.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting for which to retrieve topics
     * @return list of TopicDto objects for the meeting, empty if none exist
     */
    @Override
    public List<TopicDto> findAllByMeetingId(Integer companyId, Integer meetingId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return topicRepository.findAllByMeeting_Id(meetingId).stream()
                .map(topicMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific topic by its ID.
     * This is a read-only operation that performs access control verification.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting (for access control)
     * @param topicId   the ID of the topic to retrieve
     * @return the TopicDto for the requested topic
     * @throws TopicNotFound if topic with given ID doesn't exist
     */
    @Override
    public TopicDto findById(Integer companyId, Integer meetingId, Integer topicId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return topicMapper.toDto(topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId)));
    }
}