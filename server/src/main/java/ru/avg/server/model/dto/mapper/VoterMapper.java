package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.participant.MeetingParticipantNotFound;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoteTypeNotFound;
import ru.avg.server.exception.voting.VotingNotFound;
import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.repository.voting.VotingRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link Voter} entities and their corresponding DTOs ({@link VoterDto}).
 * This component handles bidirectional mapping while ensuring referential integrity and efficient resolution of associated entities.
 * It is designed to be injected into services that require transformation of voting participant data.
 *
 * <p>The mapper uses a pre-built map for O(1) lookup of {@link VoteType} by its title, improving performance over linear search.
 * It validates the existence of all required associations (participant, topic, voting) during the mapping process.</p>
 */
@Component
@RequiredArgsConstructor
public class VoterMapper {

    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingParticipantMapper meetingParticipantMapper;
    private final TopicRepository topicRepository;
    private final VotingRepository votingRepository;

    /**
     * Immutable map used for fast O(1) lookup of {@link VoteType} enum constants by their title.
     * This map is initialized at class loading time from all values of the {@link VoteType} enum,
     * using the title (as returned by {@link VoteType#getTitle()}) as the key.
     * It eliminates the need for stream-based filtering during DTO-to-entity conversion, significantly improving performance.
     */
    private static final Map<String, VoteType> VOTE_TYPE_MAP = Arrays.stream(VoteType.values())
            .collect(Collectors.toMap(VoteType::getTitle, Function.identity()));

    /**
     * Converts a {@link VoterDto} object into a persistent {@link Voter} entity.
     * This method validates the input DTO and resolves all associated entities by their IDs:
     * meeting participant, topic, and voting session. It also maps the vote type from its string representation
     * to the corresponding {@link VoteType} enum using an efficient lookup map.
     *
     * @param voterDto the data transfer object to convert; must not be null
     * @return a fully constructed and populated {@link Voter} entity ready for persistence
     * @throws IllegalArgumentException   if the provided {@code voterDto} is null or contains a null participant
     * @throws MeetingParticipantNotFound if no meeting participant exists with the ID specified in the DTO's participant
     * @throws TopicNotFound              if no topic exists with the ID specified in the DTO
     * @throws VotingNotFound             if no voting session exists with the ID specified in the DTO
     * @throws VoteTypeNotFound           if the vote type string in the DTO does not match any known {@link VoteType} title
     */
    public Voter fromDto(VoterDto voterDto) {
        if (voterDto == null) {
            throw new IllegalArgumentException("VoterDto must not be null");
        }
        if (voterDto.getParticipant() == null) {
            throw new IllegalArgumentException("Participant in VoterDto must not be null");
        }
        VoteType voteType = VOTE_TYPE_MAP.get(voterDto.getVote());
        if (voteType == null) {
            throw new VoteTypeNotFound(voterDto.getVote());
        }

        return Voter.builder()
                .id(voterDto.getId())
                .isRelatedPartyDeal(voterDto.isRelatedPartyDeal())
                .participant(meetingParticipantRepository.findById(voterDto.getParticipant().getId())
                        .orElseThrow(() -> new MeetingParticipantNotFound(voterDto.getParticipant().getId())))
                .topic(topicRepository.findById(voterDto.getTopicId())
                        .orElseThrow(() -> new TopicNotFound(voterDto.getTopicId())))
                .voting(votingRepository.findById(voterDto.getVotingId())
                        .orElseThrow(() -> new VotingNotFound(voterDto.getVotingId())))
                .vote(voteType)
                .build();
    }

    /**
     * Converts a persistent {@link Voter} entity into a {@link VoterDto} for use in API responses or external layers.
     * This method extracts the necessary data from the entity and its associations, including mapping the {@link VoteType}
     * enum to its string title representation. It ensures that all required relationships are present before mapping.
     *
     * @param voter the entity to convert; must not be null and must have valid associations to participant, topic, and voting
     * @return a fully populated {@link VoterDto} with all relevant fields, including IDs of associated entities and vote type title
     * @throws IllegalArgumentException if the provided {@code voter} is null or any of its required associations (participant, topic, voting) are missing
     */
    public VoterDto toDto(Voter voter) {
        if (voter == null) {
            throw new IllegalArgumentException("Voter must not be null");
        }
        if (voter.getParticipant() == null) {
            throw new IllegalArgumentException("Voter must have an associated participant");
        }
        if (voter.getTopic() == null) {
            throw new IllegalArgumentException("Voter must be associated with a topic");
        }
        if (voter.getVoting() == null) {
            throw new IllegalArgumentException("Voter must be associated with a voting session");
        }

        return VoterDto.builder()
                .id(voter.getId())
                .vote(voter.getVote().getTitle())
                .isRelatedPartyDeal(voter.isRelatedPartyDeal())
                .participant(meetingParticipantMapper.toDto(voter.getParticipant()))
                .topicId(voter.getTopic().getId())
                .votingId(voter.getVoting().getId())
                .build();
    }
}