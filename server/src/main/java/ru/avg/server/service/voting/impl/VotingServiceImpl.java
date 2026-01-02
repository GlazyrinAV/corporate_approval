package ru.avg.server.service.voting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.voting.VotingNotFound;
import ru.avg.server.model.dto.voting.VoterDto;
import ru.avg.server.model.dto.voting.VotingDto;
import ru.avg.server.model.dto.voting.mapper.VotingMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.voting.VotingRepository;
import ru.avg.server.service.voting.VoterService;
import ru.avg.server.service.voting.VotingService;
import ru.avg.server.utils.verifier.Verifier;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of the VotingService interface that manages voting sessions for corporate meetings.
 * This service handles the complete lifecycle of voting sessions including creation, vote processing,
 * and result calculation based on meeting type and corporate governance rules.
 * <p>
 * The service supports different voting mechanisms:
 * <ul>
 *   <li>Board of Directors (BOD): Simple majority based on number of participants</li>
 *   <li>Shareholder meetings (OOO, AO): Majority based on share percentages</li>
 * </ul>
 * </p>
 * <p>
 * Thread Safety: This class is not thread-safe as it maintains no shared mutable state beyond the injected
 * dependencies, which are expected to be thread-safe Spring-managed beans.
 * </p>
 *
 * @see VotingService
 * @see Voting
 * @see VotingRepository
 * @author AVG
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class VotingServiceImpl implements VotingService {

    /**
     * Repository for managing persistence operations on Voting entities.
     * Handles CRUD operations and custom queries for voting session data.
     * Injected by Spring through constructor injection.
     *
     * @see VotingRepository
     */
    private final VotingRepository votingRepository;

    /**
     * Mapper for converting between Voting entities and VotingDto objects.
     * Handles the transformation of domain entities to data transfer objects
     * for external communication and API responses.
     *
     * @see VotingMapper
     */
    private final VotingMapper votingMapper;

    /**
     * Service for managing individual voters within a voting session.
     * Handles vote submission, voter creation, and related-party transaction management.
     *
     * @see VoterService
     */
    private final VoterService voterService;

    /**
     * Utility component for verifying company and meeting relationships.
     * Ensures data isolation and access control by validating that operations
     * are performed within the correct company and meeting context.
     *
     * @see Verifier
     */
    private final Verifier verifier;

    /**
     * Creates a new voting session for a topic or returns an existing one.
     * <p>
     * This method ensures that every topic has exactly one voting session associated with it.
     * If a voting session already exists for the topic, it returns the existing session.
     * Otherwise, it creates a new voting session with initial status set to not accepted.
     * </p>
     * <p>
     * After ensuring the voting session exists, it initializes voter records for all
     * eligible participants in the meeting through the VoterService.
     * </p>
     *
     * @param topic the topic for which to create or retrieve a voting session; must not be null
     *              and must have a valid ID
     * @return a {@link VotingDto} representing the voting session for the topic; never null
     * @throws IllegalArgumentException if topic is null or has null ID
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the topic's meeting does not exist
     * @see VoterService#save(Voting) for voter initialization
     * @see VotingRepository#findByTopicId(Integer) for existing session lookup
     */
    @Override
    public VotingDto save(Topic topic) {
        Objects.requireNonNull(topic, "Topic must not be null");
        Objects.requireNonNull(topic.getId(), "Topic ID must not be null");

        // Find existing voting or create new one
        Voting voting = votingRepository.findByTopicId(topic.getId())
                .orElseGet(() -> votingRepository.save(Voting.builder()
                        .topic(topic)
                        .isAccepted(false)
                        .build()));

        // Initialize voters for the voting session
        voterService.save(voting);
        return votingMapper.toDto(voting);
    }

    /**
     * Deletes a voting session and all associated data by topic ID.
     * <p>
     * This method permanently removes the voting session associated with the specified topic.
     * It also removes all voter records associated with the voting session through cascade deletion
     * configured in the entity relationships.
     * </p>
     * <p>
     * This operation should be used when a topic is removed from the agenda before voting
     * has concluded or when the voting session needs to be canceled.
     * </p>
     *
     * @param topicId the ID of the topic whose voting session should be deleted; must not be null
     * @throws IllegalArgumentException if topicId is null
     * @see VotingRepository#deleteByTopicId(Integer) for the persistence operation
     */
    @Override
    public void deleteByTopicId(Integer topicId) {
        Objects.requireNonNull(topicId, "Topic ID must not be null");

        votingRepository.deleteByTopicId(topicId);
    }

    /**
     * Retrieves a voting session by its associated topic ID.
     * <p>
     * This method provides access to complete voting session information including:
     * <ul>
     *   <li>Voting session status (accepted/rejected)</li>
     *   <li>List of all voters and their vote statuses</li>
     *   <li>Topic and meeting associations</li>
     *   <li>Vote counts and results</li>
     * </ul>
     * </p>
     * <p>
     * The method returns the current state of the voting session, including any votes
     * that have been cast by participants. It can be used to track voting progress
     * and display real-time results to authorized users.
     * </p>
     *
     * @param topicId the ID of the topic for which to retrieve the voting session; must not be null
     * @return a {@link VotingDto} with complete information about the voting session; never null
     * @throws IllegalArgumentException if topicId is null
     * @throws VotingNotFound if no voting session exists for the specified topic ID
     */
    @Override
    public VotingDto findByTopicId(Integer topicId) {
        Objects.requireNonNull(topicId, "Topic ID must not be null");

        return votingMapper.toDto(votingRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingNotFound(topicId)));
    }

    /**
     * Processes vote submissions and determines the voting outcome.
     * <p>
     * This method handles the complete vote collection process for a topic, including:
     * <ul>
     *   <li>Validating company and meeting context</li>
     *   <li>Processing individual vote submissions from participants</li>
     *   <li>Calculating voting results based on corporate rules</li>
     *   <li>Determining whether the vote is accepted or rejected</li>
     *   <li>Updating the voting session status accordingly</li>
     * </ul>
     * </p>
     * <p>
     * The voting rules depend on the meeting type:
     * <ul>
     *   <li>Board of Directors (BOD): Simple majority (>50% of voters)</li>
     *   <li>Shareholder meetings (OOO, AO): Majority based on share percentages (>50% of shares)</li>
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
     * @throws VotingNotFound if no voting session exists for the topic
     * @throws ru.avg.server.exception.voting.VoteTypeNotFound if a submitted vote type is invalid
     * @see #checkVoting(Integer, List) for the voting calculation logic
     */
    @Override
    public VotingDto makeVote(Integer companyId, Integer meetingId, Integer topicId, List<VoterDto> voters) {
        Objects.requireNonNull(companyId, "Company ID must not be null");
        Objects.requireNonNull(meetingId, "Meeting ID must not be null");
        Objects.requireNonNull(topicId, "Topic ID must not be null");
        Objects.requireNonNull(voters, "Voters list must not be null");

        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        // Process all votes
        voters.forEach(voterService::makeVote);

        // Check voting results and update status
        checkVoting(topicId, voters);

        return findByTopicId(topicId);
    }

    /**
     * Calculates voting results based on meeting type and updates voting status.
     * <p>
     * This private method implements the business logic for determining voting outcomes
     * according to corporate governance rules. The calculation method depends on the meeting type:
     * </p>
     * <ul>
     *   <li><b>Board of Directors (BOD):</b>
     *       Uses simple majority rule where approval requires more than 50% of the total votes cast.
     *       Each director counts as one vote regardless of share ownership.
     *   </li>
     *   <li><b>Shareholder meetings (OOO, AO):</b>
     *       Uses share-based majority rule where approval requires more than 50% of the total shares
     *       represented in the voting session. Each participant's vote weight is proportional to
     *       their share percentage in the company.
     *   </li>
     * </ul>
     * <p>
     * The method:
     * <ol>
     *   <li>Retrieves the voting session from the repository</li>
     *   <li>Determines the meeting type from the associated meeting</li>
     *   <li>Calculates the total approval count based on the appropriate weighting system</li>
     *   <li>Determines acceptance based on the majority threshold</li>
     *   <li>Updates and persists the voting session status</li>
     * </ol>
     * </p>
     *
     * @param topicId the ID of the topic being voted on; must not be null
     * @param voters a list of voters who have cast their votes; must not be null
     * @throws IllegalArgumentException if topicId or voters is null
     * @throws VotingNotFound if no voting session exists for the specified topic ID
     * @see MeetingType for supported meeting types
     * @see VoteType#YES for approval votes
     */
    private void checkVoting(Integer topicId, List<VoterDto> voters) {
        Objects.requireNonNull(topicId, "Topic ID must not be null");
        Objects.requireNonNull(voters, "Voters list must not be null");

        Voting voting = votingRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingNotFound(topicId));

        MeetingType meetingType = voting.getTopic().getMeeting().getType();

        double approvalCount = voters.stream()
                .filter(voter -> VoteType.YES.toString().equals(voter.getVote()))
                .mapToDouble(voter -> {
                    if (meetingType == MeetingType.BOD) {
                        return 1.0; // Each director counts as 1 vote
                    } else {
                        // For shareholder meetings, use share percentage
                        return voter.getParticipant().getParticipant().getShare();
                    }
                })
                .sum();

        boolean isAccepted;
        if (meetingType == MeetingType.BOD) {
            // Board of Directors: simple majority (>50% of voters)
            long totalVoters = voters.size();
            isAccepted = approvalCount > totalVoters * 0.5;
        } else {
            // Shareholder meetings: require >50% of shares
            isAccepted = approvalCount > 50.0;
        }

        voting.setAccepted(isAccepted);
        votingRepository.save(voting);
    }
}