package ru.avg.server.service.topic;

import org.springframework.data.domain.Page;
import ru.avg.server.model.dto.topic.NewTopicDto;
import ru.avg.server.model.dto.topic.TopicDto;

/**
 * Service interface defining the contract for managing topics within the approval system.
 * This interface provides methods for creating, updating, deleting, and retrieving topics
 * associated with meetings, with all operations scoped to a specific company and meeting context.
 * <p>
 * The service enforces business rules and access control by requiring both companyId and meetingId
 * for all operations, ensuring that topics can only be managed within the proper organizational
 * and meeting hierarchy. This prevents unauthorized access and maintains data integrity.
 * </p>
 *
 * @author AVG
 * @since 1.0
 */
public interface TopicService {

    /**
     * Creates a new topic for a specific meeting within a company.
     * The topic is associated with the specified meeting and becomes part of the meeting's agenda.
     * Validation is performed to ensure the meeting exists and the topic data is valid.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting for which the topic is being created
     * @param topicDto  the DTO containing the topic data to be saved, must not be null
     * @return the saved TopicDto with the generated ID and other system-assigned fields
     * @throws IllegalArgumentException                        if topicDto is null
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    TopicDto save(Integer companyId, Integer meetingId, NewTopicDto topicDto);

    /**
     * Updates an existing topic with new information.
     * Only the fields provided in the topicDto are updated (partial update semantics).
     * The topic must exist and belong to the specified meeting and company.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting to which the topic belongs
     * @param topicId   the ID of the topic to update
     * @param topicDto  the DTO containing the fields to update, must not be null
     * @return the updated TopicDto reflecting the changes in the database
     * @throws IllegalArgumentException                        if topicDto is null
     * @throws ru.avg.server.exception.topic.TopicNotFound     if the specified topic does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    TopicDto update(Integer companyId, Integer meetingId, Integer topicId, NewTopicDto topicDto);

    /**
     * Deletes a topic identified by its ID from a specific meeting.
     * The topic is removed from the meeting's agenda and deleted from the system.
     * This operation is permanent and cannot be undone.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting from which the topic will be removed
     * @param topicId   the ID of the topic to delete
     * @throws ru.avg.server.exception.topic.TopicNotFound     if the specified topic does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    void delete(Integer companyId, Integer meetingId, Integer topicId);

    /**
     * Retrieves all topics associated with a specific meeting.
     * Returns topics in the order they should appear in the meeting agenda.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting for which to retrieve topics
     * @return a list of TopicDto objects representing all topics for the meeting,
     * ordered by their position in the agenda; returns empty list if no topics exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    Page<TopicDto> findAllByMeetingId(Integer companyId, Integer meetingId, Integer page, Integer limit);

    /**
     * Retrieves a specific topic by its ID within the context of a meeting and company.
     * Used to get detailed information about a single topic.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting to which the topic belongs
     * @param topicId   the ID of the topic to retrieve
     * @return the TopicDto representing the requested topic
     * @throws ru.avg.server.exception.topic.TopicNotFound     if the specified topic does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    TopicDto findById(Integer companyId, Integer meetingId, Integer topicId);

    /**
     * Searches for topics by a search criteria within a specific meeting.
     * Performs a case-insensitive partial match search on topic titles.
     * Returns paginated results to support efficient handling of large datasets.
     *
     * @param companyId the ID of the company to which the meeting belongs (used for access control)
     * @param meetingId the ID of the meeting in which to search for topics
     * @param criteria  the search string to match against topic titles; if blank or null, returns empty page
     * @param page      the zero-based page number to retrieve; must be non-negative
     * @param limit     the maximum number of elements to return per page; must be between 1 and 20 (inclusive)
     * @return a Page of TopicDto objects matching the search criteria, including full pagination metadata;
     * never null, returns empty page if no results found or criteria is blank
     * @throws IllegalArgumentException                        if page is negative or limit is not in range [1,20]
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    Page<TopicDto> findByCriteria(Integer companyId, Integer meetingId, String criteria, Integer page, Integer limit);
}