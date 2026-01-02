package ru.avg.server.service.voting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.voting.VoteTypeNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.model.dto.voting.VoterDto;
import ru.avg.server.model.dto.voting.mapper.VoterMapper;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.voting.VoterRepository;
import ru.avg.server.service.voting.VoterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link VoterService} interface for managing voters in voting sessions.
 * This service handles the creation, updating, retrieval, and processing of votes for participants
 * in corporate governance meetings. It ensures that all voters are properly initialized for a voting
 * session and manages the lifecycle of vote submissions.
 *
 * <p>The implementation uses dependency injection to obtain necessary repositories and mappers,
 * following Spring's best practices for service layer design. The class is annotated with
 * {@link Service} to mark it as a Spring-managed bean and {@link RequiredArgsConstructor}
 * to generate a constructor for all final fields, enabling compile-time null safety.</p>
 *
 * @see VoterService
 * @see VoterRepository
 * @see MeetingParticipantRepository
 * @see VoterMapper
 * @author AVG
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {

    /**
     * Repository for managing persistence operations on {@link Voter} entities.
     * Provides CRUD operations and custom queries for voter data storage and retrieval.
     * Injected by Spring through constructor injection.
     *
     * @see VoterRepository
     */
    private final VoterRepository voterRepository;

    /**
     * Repository for accessing {@link MeetingParticipant} entities.
     * Used to retrieve all participants in a meeting when initializing voters for a new voting session.
     * Injected by Spring through constructor injection.
     *
     * @see MeetingParticipantRepository
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Mapper for converting between {@link Voter} entities and {@link VoterDto} objects.
     * Handles the transformation of domain entities to data transfer objects for external communication.
     * Injected by Spring through constructor injection.
     *
     * @see VoterMapper
     */
    private final VoterMapper voterMapper;

    /**
     * Creates initial voter records for a voting session based on the associated topic.
     * <p>
     * This method ensures that every participant in the meeting has a corresponding voter record
     * for the specified voting session. It first identifies existing voters to avoid duplication,
     * then creates new voter records for any participants who don't already have one.
     * </p>
     * <p>
     * The method follows these steps:
     * <ol>
     *   <li>Extracts existing participant IDs from current voters for O(1) lookup</li>
     *   <li>Retrieves all participants from the meeting associated with the voting topic</li>
     *   <li>Creates new voter records for participants without existing voters</li>
     *   <li>Sets initial vote status to NOT_VOTED for new voters</li>
     *   <li>Returns the list of existing voter DTOs</li>
     * </ol>
     * </p>
     *
     * @param voting the voting session for which to create voter records; must not be null
     *               and must have a valid topic and meeting association
     * @return a list of existing {@link VoterDto} objects; never null but may be empty
     *         if no voters exist for the voting session
     * @throws IllegalArgumentException if voting is null or has invalid associations
     * @see #makeVote(VoterDto) for processing vote submissions
     * @see VoteType#NOT_VOTED for the initial vote status
     */
    @Override
    public List<VoterDto> save(Voting voting) {
        // Extract existing voter participants as a Set for O(1) lookup
        Set<Integer> existingParticipantIds = voting.getVoters() != null
                ? voting.getVoters().stream()
                .map(voter -> voter.getParticipant().getId())
                .collect(Collectors.toSet())
                : Set.of();

        // Get all meeting participants
        List<MeetingParticipant> allParticipants = meetingParticipantRepository
                .findAllByMeetingId(voting.getTopic().getMeeting().getId());

        // Create voters for participants who don't already have one
        if (allParticipants != null) {
            allParticipants.stream()
                    .filter(participant -> !existingParticipantIds.contains(participant.getId()))
                    .map(participant -> Voter.builder()
                            .voting(voting)
                            .participant(participant)
                            .vote(VoteType.NOT_VOTED)
                            .topic(voting.getTopic())
                            .build())
                    .forEach(voterRepository::save);
        }

        // Return existing voters (if any)
        return voting.getVoters() != null
                ? voting.getVoters().stream()
                .map(voterMapper::toDto)
                .toList()
                : new ArrayList<>();
    }

    /**
     * Updates an existing voter record with new information.
     * <p>
     * This method allows modification of voter properties such as related-party transaction status.
     * It only updates the record if the related-party deal status has changed to avoid unnecessary
     * persistence operations. The method ensures data consistency by retrieving the existing record,
     * applying changes, and saving the updated entity.
     * </p>
     *
     * @param voterDto the updated voter data containing the voter ID and new values; must not be null
     * @return the updated {@link VoterDto} with current values from the persistence layer
     * @throws IllegalArgumentException if voterDto is null or has null ID
     * @throws VoterNotFound if no voter exists with the specified ID
     * @see Voter#isRelatedPartyDeal() for the related-party transaction status
     */
    @Override
    public VoterDto update(VoterDto voterDto) {
        Voter voter = voterRepository.findById(voterDto.getId())
                .orElseThrow(() -> new VoterNotFound(voterDto.getId()));

        if (voterDto.isRelatedPartyDeal() != voter.isRelatedPartyDeal()) {
            voter.setRelatedPartyDeal(voterDto.isRelatedPartyDeal());
        }
        return voterMapper.toDto(voterRepository.save(voter));
    }

    /**
     * Removes a voter record from the system by its ID.
     * <p>
     * This method permanently deletes a voter record. It should be used with caution as
     * it removes the voter from the persistence layer without any confirmation or validation
     * of voting status. The responsibility for ensuring the deletion is appropriate lies
     * with the calling service.
     * </p>
     *
     * @param voterId the ID of the voter record to delete; must not be null
     * @throws IllegalArgumentException if voterId is null
     * @see #delete(Integer) for the implementation
     */
    @Override
    public void delete(Integer voterId) {
        voterRepository.deleteById(voterId);
    }

    /**
     * Retrieves a specific voter record by its ID.
     * <p>
     * This method provides access to complete voter information including vote status,
     * related-party transaction status, participant details, and topic associations.
     * It converts the persistence entity to a DTO for external use.
     * </p>
     *
     * @param voterId the ID of the voter to retrieve; must not be null
     * @return the requested {@link VoterDto} with complete information
     * @throws IllegalArgumentException if voterId is null
     * @throws VoterNotFound if no voter exists with the specified ID
     */
    @Override
    public VoterDto find(Integer voterId) {
        return voterMapper.toDto(voterRepository.findById(voterId)
                .orElseThrow(() -> new VoterNotFound(voterId)));
    }

    /**
     * Retrieves all voter records associated with a specific agenda topic.
     * <p>
     * This method is useful for displaying voting roll calls, calculating voting statistics,
     * generating reports on participation, and validating quorum requirements. The results
     * include all voters regardless of whether they have cast their vote, allowing consumers
     * to distinguish between participants who voted and those who did not.
     * </p>
     *
     * @param topicId the ID of the topic for which to retrieve voters; must not be null
     * @return a list of {@link VoterDto} objects representing all voters for the topic;
     *         never null but may be empty if no voters exist for the topic
     * @throws IllegalArgumentException if topicId is null
     */
    @Override
    public List<VoterDto> findAllByTopicId(Integer topicId) {
        return voterRepository.findAllByTopic_Id(topicId).stream()
                .map(voterMapper::toDto)
                .toList();
    }

    /**
     * Records a participant's vote in a voting session.
     * <p>
     * This method processes the actual vote submission from a participant. It updates
     * the voter record with the specified vote type and handles any related business logic.
     * The method always updates the vote to ensure consistency, even if the vote type
     * hasn't changed, to maintain data integrity.
     * </p>
     * <p>
     * The method follows these steps:
     * <ol>
     *   <li>Retrieves the existing voter record by ID</li>
     *   <li>Validates that the vote type is valid by looking up the VoteType enum</li>
     *   <li>Updates the voter's vote status</li>
     *   <li>Persists the updated record</li>
     * </ol>
     * </p>
     *
     * @param voterDto the vote submission containing the voter ID and vote type;
     *                 must not be null and must have valid ID and vote type
     * @throws IllegalArgumentException if voterDto is null or has invalid data
     * @throws VoterNotFound if no voter exists with the specified ID
     * @throws VoteTypeNotFound if the specified vote type is invalid or not supported
     * @see VoteType for valid vote type constants
     */
    @Override
    public void makeVote(VoterDto voterDto) {
        Voter voter = voterRepository.findById(voterDto.getId())
                .orElseThrow(() -> new VoterNotFound(voterDto.getId()));

        // Always update the vote regardless of current state to ensure consistency
        VoteType voteType = Arrays.stream(VoteType.values())
                .filter(vote -> vote.getTitle().equals(voterDto.getVote()))
                .findFirst()
                .orElseThrow(() -> new VoteTypeNotFound(voterDto.getVote()));

        voter.setVote(voteType);
        voterRepository.save(voter);
    }
}