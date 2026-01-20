package ru.avg.server.service.voting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.exception.voting.VotingNotFound;
import ru.avg.server.model.dto.voting.VoterDto;
import ru.avg.server.model.dto.voting.VotingDto;
import ru.avg.server.model.dto.voting.mapper.VotingMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.voting.VoterRepository;
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
 * @author AVG
 * @see VotingService
 * @see Voting
 * @see VotingRepository
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

    private final VoterRepository voterRepository;

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
     * @throws IllegalArgumentException                        if topic is null or has null ID
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
     * @throws VotingNotFound           if no voting session exists for the specified topic ID
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
     * @param topicId   the ID of the topic being voted on; must not be null
     * @param voters    a list of {@link VoterDto} objects containing vote submissions;
     *                  must not be null but may be empty
     * @return a {@link VotingDto} representing the updated voting session with final
     * results and status; never null
     * @throws IllegalArgumentException                        if any parameter is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the meeting does not exist
     * @throws ru.avg.server.exception.topic.TopicNotFound     if the topic does not exist
     * @throws VotingNotFound                                  if no voting session exists for the topic
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
     * Validates and processes voting results for a specific topic to determine acceptance.
     * This method calculates the outcome of a voting session based on the provided votes,
     * applying different rules depending on the meeting type (Board of Directors vs. Shareholders).
     * <p>
     * For Board of Directors meetings ({@link MeetingType#BOD}):
     * <ul>
     *   <li>Each participant counts as one vote regardless of shareholding</li>
     *   <li>Decision is accepted if more than 50% of participants vote "YES"</li>
     *   <li>Simple majority rule applies</li>
     * </ul>
     * </p>
     * <p>
     * For Shareholder meetings ({@link MeetingType#FMS}, {@link MeetingType#FMP}):
     * <ul>
     *   <li>Vote weight is determined by the participant's share percentage</li>
     *   <li>Decision is accepted if "YES" votes represent more than 50% of total shares</li>
     *   <li>Share-based majority rule applies</li>
     * </ul>
     * </p>
     * <p>
     * The method performs the following operations:
     * <ol>
     *   <li>Validates that topicId and voters list are not null</li>
     *   <li>Retrieves the voting session associated with the topic</li>
     *   <li>Determines the meeting type to apply appropriate rules</li>
     *   <li>Calculates total approval weight from "YES" votes</li>
     *   <li>Sets the acceptance status based on meeting-type-specific rules</li>
     *   <li>Persists the updated voting status</li>
     * </ol>
     * </p>
     *
     * @param topicId the unique identifier of the topic being voted on; must not be null
     * @param voters  a list of {@link VoterDto} objects representing all votes cast in the session;
     *                must not be null and should contain at least one vote
     * @throws IllegalArgumentException if topicId or voters is null
     * @throws VotingNotFound           if no voting session exists for the given topicId
     * @throws VoterNotFound            if a voter referenced in the list cannot be found in the repository
     * @see VotingRepository#findByTopicId(Integer)
     * @see MeetingType
     * @see VoteType
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
                        Voter currentVoter = voterRepository.findById(voter.getMeetingParticipantId()).orElseThrow(
                                () -> new VoterNotFound(voter.getMeetingParticipantId())
                        );

                        return currentVoter.getMeetingParticipant().getParticipant().getShare();
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