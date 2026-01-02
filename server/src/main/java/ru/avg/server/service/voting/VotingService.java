package ru.avg.server.service.voting;

import ru.avg.server.model.dto.voting.VoterDto;
import ru.avg.server.model.dto.voting.VotingDto;
import ru.avg.server.model.topic.Topic;

import java.util.List;

/**
 * Service interface for managing voting sessions in the corporate approval system.
 * This interface defines operations for creating, retrieving, and managing voting sessions
 * associated with agenda topics, including processing votes from participants.
 * <p>
 * The service handles the complete lifecycle of a voting session, from creation when
 * a topic is added to the agenda, through vote collection, to final result determination.
 * It ensures proper isolation between different companies and meetings, maintaining
 * data integrity and access control throughout the voting process.
 * </p>
 *
 * @see VotingDto
 * @see VoterDto
 * @see Topic
 * @author AVG
 * @since 1.0
 */
public interface VotingService {

    /**
     * Creates a new voting session for a specified agenda topic.
     * <p>
     * This method initiates the voting process for a topic by creating a voting session
     * and initializing voter records for all eligible participants. The voting session
     * is linked to the topic and prepared for vote collection.
     * </p>
     * <p>
     * The creation process includes:
     * <ul>
     *   <li>Validating that the topic exists and belongs to an active meeting</li>
     *   <li>Creating a voting session record associated with the topic</li>
     *   <li>Initializing voter records for all eligible participants</li>
     *   <li>Setting initial voting status to "not accepted"</li>
     * </ul>
     * </p>
     *
     * @param topic the agenda topic for which to create a voting session; must not be null
     *              and must have a valid meeting association
     * @return a {@link VotingDto} representing the created voting session with initialized
     *         voter list and default status; never null
     * @throws IllegalArgumentException if topic is null or has invalid associations
     * @throws ru.avg.server.exception.topic.TopicNotFound if the topic does not exist
     * @throws ru.avg.server.exception.voting.VotingAlreadyExist if a voting session already exists for the topic
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the topic's meeting does not exist
     */
    VotingDto save(Topic topic);

    /**
     * Deletes the voting session associated with a specific topic.
     * <p>
     * This method permanently removes a voting session and all associated voter records
     * by the topic ID. It should only be used when the voting session needs to be canceled
     * or when the topic is being removed from the agenda before voting has concluded.
     * </p>
     * <p>
     * The deletion process:
     * <ul>
     *   <li>Validates that the topic exists</li>
     *   <li>Finds the voting session associated with the topic</li>
     *   <li>Removes all voter records for the voting session</li>
     *   <li>Removes the voting session record</li>
     * </ul>
     * </p>
     *
     * @param topicId the ID of the topic whose voting session should be deleted; must not be null
     * @throws IllegalArgumentException if topicId is null
     * @throws ru.avg.server.exception.topic.TopicNotFound if no topic exists with the specified ID
     * @throws IllegalStateException if deletion is not allowed due to voting activity
     */
    void deleteByTopicId(Integer topicId);

    /**
     * Retrieves the voting session associated with a specific agenda topic.
     * <p>
     * This method provides access to complete voting session information including:
     * <ul>
     *   <li>Voting session status (accepted/rejected)</li>
     *   <li>List of all voters and their vote statuses</li>
     *   <li>Topic and meeting associations</li>
     *   <li>Vote counts and results (when available)</li>
     * </ul>
     * </p>
     * <p>
     * The method returns the current state of the voting session, including any votes
     * that have been cast by participants. It can be used to track voting progress
     * and display real-time results to authorized users.
     * </p>
     *
     * @param topicId the ID of the topic for which to retrieve the voting session; must not be null
     * @return a {@link VotingDto} with complete information about the voting session;
     *         never null
     * @throws IllegalArgumentException if topicId is null
     * @throws ru.avg.server.exception.topic.TopicNotFound if no topic exists with the specified ID
     * @throws ru.avg.server.exception.voting.VotingNotFound if no voting session exists for the topic
     */
    VotingDto findByTopicId(Integer topicId);

    /**
     * Processes vote submissions for a voting session and determines the outcome.
     * <p>
     * This method handles the complete vote collection process for a topic, including:
     * <ul>
     *   <li>Validating company and meeting context</li>
     *   <li>Processing individual vote submissions from participants</li>
     *   <li>Updating voter records with submitted votes</li>
     *   <li>Calculating voting results based on corporate rules</li>
     *   <li>Determining whether the vote is accepted or rejected</li>
     *   <li>Updating the voting session status accordingly</li>
     * </ul>
     * </p>
     * <p>
     * The method expects a list of {@link VoterDto} objects containing:
     * <ul>
     *   <li>Voter IDs</li>
     *   <li>Vote types (YES, NO, ABSTAINED)</li>
     *   <li>Related-party transaction status (if applicable)</li>
     * </ul>
     * </p>
     *
     * @param companyId the ID of the company that owns the meeting; must not be null
     * @param meetingId the ID of the meeting where voting occurs; must not be null
     * @param topicId the ID of the topic being voted on; must not be null
     * @param voters a list of {@link VoterDto} objects containing vote submissions;
     *               must not be null but may be empty
     * @return a {@link VotingDto} representing the updated voting session with final
     *         results and status; never null
     * @throws IllegalArgumentException if any parameter is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the meeting does not exist
     * @throws ru.avg.server.exception.topic.TopicNotFound if the topic does not exist
     * @throws ru.avg.server.exception.voting.VotingNotFound if no voting session exists for the topic
     * @throws ru.avg.server.exception.voting.VoteTypeNotFound if a submitted vote type is invalid
     * @throws IllegalStateException if the voting session is closed or votes have already been processed
     */
    VotingDto makeVote(Integer companyId, Integer meetingId, Integer topicId, List<VoterDto> voters);
}