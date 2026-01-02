package ru.avg.server.service.voting;

import ru.avg.server.model.dto.voting.VoterDto;
import ru.avg.server.model.voting.Voting;

import java.util.List;

/**
 * Service interface for managing voting participants (voters) in the corporate approval system.
 * This interface defines operations for creating, updating, retrieving, and managing votes
 * cast by participants in voting sessions.
 * <p>
 * The service handles both the structural aspects of voter management (CRUD operations)
 * and the functional aspects of casting votes, ensuring data consistency and business
 * rule enforcement throughout the voting process.
 * </p>
 * <p>
 * Implementations of this interface are responsible for:
 * <ul>
 *   <li>Creating voter records when a voting session is initiated</li>
 *   <li>Processing actual vote submissions from participants</li>
 *   <li>Maintaining referential integrity between voters, votings, topics, and participants</li>
 *   <li>Enforcing business rules around voting eligibility and related-party transactions</li>
 * </ul>
 * </p>
 *
 * @author AVG
 * @see VoterDto
 * @see Voting
 * @since 1.0
 */
public interface VoterService {

    /**
     * Creates initial voter records for a voting session based on the associated topic.
     * <p>
     * This method is typically called when a new voting session is created, generating
     * a voter record for each participant who is eligible to vote on the associated topic.
     * The voter records are initialized with default values (e.g., vote type set to NOT_VOTED)
     * and are linked to the voting session, topic, and participant.
     * </p>
     * <p>
     * The method ensures that:
     * <ul>
     *   <li>Only active participants are included as voters</li>
     *   <li>Voters are properly associated with the voting session and topic</li>
     *   <li>Default vote status is set to NOT_VOTED</li>
     *   <li>Related-party transaction flags are properly initialized</li>
     * </ul>
     * </p>
     *
     * @param voting the voting session for which to create voter records; must not be null
     *               and must have a valid topic relationship
     * @return a list of created {@link VoterDto} objects, each representing a participant
     * who can vote in the session; never null but may be empty
     * @throws IllegalArgumentException                                       if voting is null or has invalid associations
     * @throws ru.avg.server.exception.topic.TopicNotFound                    if the voting session's topic does not exist
     * @throws ru.avg.server.exception.participant.MeetingParticipantNotFound if a referenced participant does not exist
     * @see #makeVote(VoterDto) for submitting actual votes
     */
    List<VoterDto> save(Voting voting);

    /**
     * Updates an existing voter record with new information.
     * <p>
     * This method allows modification of voter properties such as related-party transaction status.
     * It does not allow changes to core associations (voting session, topic, participant) or
     * the vote type if a vote has already been cast.
     * </p>
     * <p>
     * The update process:
     * <ul>
     *   <li>Retrieves the existing voter record by ID</li>
     *   <li>Validates that the voter exists and is in an editable state</li>
     *   <li>Applies non-null values from the DTO to the existing record</li>
     *   <li>Persists the updated record</li>
     * </ul>
     * </p>
     *
     * @param voterDto the updated voter data; must contain a valid ID and may contain
     *                 updated values for related-party transaction status
     * @return the updated {@link VoterDto} with current values from the persistence layer
     * @throws IllegalArgumentException if voterDto is null or has null ID
     * @throws IllegalStateException    if attempting to modify an immutable property
     *                                  (e.g., vote type for a cast vote)
     */
    VoterDto update(VoterDto voterDto);

    /**
     * Removes a voter record from the system.
     * <p>
     * This method permanently deletes a voter record by its ID. It should only be used
     * before any votes have been cast, as deleting a voter after voting would compromise
     * the integrity of voting results.
     * </p>
     * <p>
     * The deletion process:
     * <ul>
     *   <li>Validates that the voter exists</li>
     *   <li>Optionally validates that no vote has been cast (implementation dependent)</li>
     *   <li>Removes the voter record from persistence</li>
     * </ul>
     * </p>
     *
     * @param voterId the ID of the voter record to delete; must not be null
     * @throws IllegalArgumentException if voterId is null
     * @throws IllegalStateException    if deletion is not allowed due to voting activity
     */
    void delete(Integer voterId);

    /**
     * Retrieves a specific voter record by its ID.
     * <p>
     * This method provides access to complete voter information including:
     * <ul>
     *   <li>Vote status (YES, NO, ABSTAINED, NOT_VOTED)</li>
     *   <li>Related-party transaction status</li>
     *   <li>Participant details</li>
     *   <li>Topic and voting session associations</li>
     * </ul>
     * </p>
     *
     * @param voterId the ID of the voter to retrieve; must not be null
     * @return the requested {@link VoterDto} with complete information
     * @throws IllegalArgumentException if voterId is null
     */
    VoterDto find(Integer voterId);

    /**
     * Retrieves all voter records associated with a specific agenda topic.
     * <p>
     * This method is useful for:
     * <ul>
     *   <li>Displaying voting roll calls</li>
     *   <li>Calculating voting statistics</li>
     *   <li>Generating reports on participation</li>
     *   <li>Validating quorum requirements</li>
     * </ul>
     * </p>
     * <p>
     * The results include all voters regardless of whether they have cast their vote,
     * allowing consumers to distinguish between participants who voted and those who did not.
     * </p>
     *
     * @param topicId the ID of the topic for which to retrieve voters; must not be null
     * @return a list of {@link VoterDto} objects representing all voters for the topic;
     * never null but may be empty
     * @throws IllegalArgumentException if topicId is null
     * @see #makeVote(VoterDto) for determining which voters have cast votes
     */
    List<VoterDto> findAllByTopicId(Integer topicId);

    /**
     * Records a participant's vote in a voting session.
     * <p>
     * This method processes the actual vote submission from a participant. It updates
     * the voter record with the specified vote type and handles any related business logic
     * such as:
     * <ul>
     *   <li>Validating the voter is eligible to vote</li>
     *   <li>Ensuring the voting session is still open</li>
     *   <li>Preventing multiple votes from the same participant</li>
     *   <li>Updating voting statistics and status</li>
     *   <li>Handling related-party transaction disclosures</li>
     * </ul>
     * </p>
     * <p>
     * The method expects a complete {@link VoterDto} containing:
     * <ul>
     *   <li>The voter ID</li>
     *   <li>The vote type (YES, NO, ABSTAINED)</li>
     *   <li>Related-party transaction status (if applicable)</li>
     * </ul>
     * </p>
     *
     * @param voterDto the vote submission containing the voter ID and vote type;
     *                 must not be null and must have valid ID and vote type
     * @throws IllegalArgumentException                        if voterDto is null or has invalid data
     * @throws IllegalStateException                           if the voting session is closed or the voter
     *                                                         has already cast a vote
     * @throws ru.avg.server.exception.voting.VoteTypeNotFound if the specified vote type is invalid
     */
    void makeVote(VoterDto voterDto);
}