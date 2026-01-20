package ru.avg.server.service.topic.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * Maximum page limit for pagination operations, injected from configuration property 'page.maxlimit.company'.
     * This value is used to validate and cap the limit parameter in paginated requests.
     *
     * @see #findAllByMeetingId(Integer, Integer, Integer, Integer)
     * @see #findByCriteria(Integer, Integer, String, Integer, Integer)
     * @see Verifier#verifyPageAndLimit(Integer, Integer, Integer)
     */
    @Value("${page.maxlimit.company}")
    private Integer pageLimit;

    /**
     * Repository for data access operations related to topics.
     * Handles CRUD operations and custom queries for the Topic entity.
     *
     * @see TopicRepository
     * @see Topic
     */
    private final TopicRepository topicRepository;

    /**
     * Service responsible for managing voting processes associated with topics.
     * Handles creation and management of voting records when topics are created.
     *
     * @see VotingService
     * @see #save(Integer, Integer, NewTopicDto)
     */
    private final VotingService votingService;

    /**
     * Mapper for converting Topic entities to TopicDto data transfer objects.
     * Used to transform persistent entities into API-friendly representations.
     *
     * @see TopicMapper
     * @see TopicDto
     * @see #findById(Integer, Integer, Integer)
     * @see #findAllByMeetingId(Integer, Integer, Integer, Integer)
     */
    private final TopicMapper topicMapper;

    /**
     * Mapper for converting NewTopicDto data transfer objects to Topic entities.
     * Used to transform API input into persistent entity objects for creation and update operations.
     *
     * @see NewTopicMapper
     * @see NewTopicDto
     * @see #save(Integer, Integer, NewTopicDto)
     * @see #update(Integer, Integer, Integer, NewTopicDto)
     */
    private final NewTopicMapper newTopicMapper;

    /**
     * Utility service for verifying business rules and access control.
     * Validates company and meeting existence, as well as pagination parameters.
     *
     * @see Verifier
     * @see Verifier#verifyCompanyAndMeeting(Integer, Integer)
     * @see Verifier#verifyPageAndLimit(Integer, Integer, Integer)
     */
    private final Verifier verifier;

    /**
     * Utility service for updating entity fields.
     * Handles partial updates by copying non-null fields from source to target entity.
     *
     * @see Updater
     * @see #update(Integer, Integer, Integer, NewTopicDto)
     */
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
     * Retrieves a paginated list of all topics associated with a specific meeting,
     * sorted by topic title in ascending order.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting for which to retrieve topics
     * @param page      the zero-based page number to retrieve; must be non-negative
     * @param limit     the maximum number of elements to return per page; must be between 1 and 20 (inclusive)
     * @return a {@link Page} of {@link TopicDto} objects for the requested page,
     * including full pagination metadata (total elements, total pages, etc.);
     * never {@code null}
     * @throws IllegalArgumentException if {@code page} is negative or {@code limit} is not in the range [1,20]
     * @see TopicRepository#findAllByMeetingIdOrderByTitle(Integer, Pageable)
     */
    @Override
    public Page<TopicDto> findAllByMeetingId(Integer companyId, Integer meetingId, Integer page, Integer limit) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        verifier.verifyPageAndLimit(page, limit, pageLimit);

        Pageable pageable = PageRequest.of(page, limit);

        return topicRepository.findAllByMeetingIdOrderByTitle(meetingId, pageable)
                .map(topicMapper::toDto);
    }

    /**
     * Retrieves a specific topic by its ID within the context of a company and meeting.
     * Performs access control verification before retrieving the topic.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting (for access control)
     * @param topicId   the ID of the topic to retrieve
     * @return the {@link TopicDto} for the requested topic
     * @throws TopicNotFound if topic with given ID doesn't exist or is not accessible
     */
    @Override
    public TopicDto findById(Integer companyId, Integer meetingId, Integer topicId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return topicMapper.toDto(topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFound(topicId)));
    }

    /**
     * Searches for topics within a specific meeting based on a text criteria.
     * The search performs a case-insensitive partial match on topic titles.
     * Returns results in a paginated format sorted by title in ascending order.
     *
     * @param companyId the ID of the company (for access control)
     * @param meetingId the ID of the meeting to search within
     * @param criteria  the search string to match against topic titles;
     *                  if null or blank, returns an empty page to prevent full dataset retrieval
     * @param page      the zero-based page number to retrieve; must be non-negative
     * @param limit     the maximum number of elements to return per page; must be between 1 and 20 (inclusive)
     * @return a {@link Page} of {@link TopicDto} objects matching the search criteria
     * for the requested page, including full pagination metadata;
     * never {@code null}
     * @throws IllegalArgumentException if {@code page} is negative or {@code limit} is not in the range [1,20]
     * @see TopicRepository#findByCriteria(Integer, String, Pageable)
     */
    @Override
    public Page<TopicDto> findByCriteria(Integer companyId, Integer meetingId, String criteria, Integer page, Integer limit) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        verifier.verifyPageAndLimit(page, limit, pageLimit);

        // Return empty page for null or blank criteria to prevent unintended full dataset retrieval
        if (criteria == null || criteria.isBlank()) {
            return Page.empty(PageRequest.of(page, limit));
        }

        Pageable pageable = PageRequest.of(page, limit);

        return topicRepository.findByCriteria(meetingId, criteria, pageable)
                .map(topicMapper::toDto);
    }
}