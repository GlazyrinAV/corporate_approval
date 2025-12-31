package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.repository.voting.VoterRepository;

import java.util.List;

/**
 * Mapper class responsible for converting between {@link Voting} entities and {@link VotingDto} data transfer objects.
 * This component ensures bidirectional transformation while maintaining referential integrity and efficiently handling
 * collections of voters. It is a Spring-managed bean, designed to be injected into services that require voting data mapping.
 */
@Component
@RequiredArgsConstructor
public class VotingMapper {

    private final TopicRepository topicRepository;
    private final VoterRepository voterRepository;

    /**
     * Converts a {@link VotingDto} into a persistent {@link Voting} entity.
     * Resolves the associated topic using the topic ID from the DTO and fetches all voters by their IDs.
     * If any referenced voter or topic does not exist, an appropriate exception is thrown.
     *
     * @param votingDto the data transfer object to convert; must not be null
     * @return a fully constructed {@link Voting} entity with topic and voter associations populated
     * @throws IllegalArgumentException if the provided {@code votingDto} is null or contains a null topic ID
     * @throws TopicNotFound            if no topic exists with the specified topic ID
     * @throws VoterNotFound            if any of the voter IDs in the DTO do not correspond to existing voter records
     */
    public Voting fromDto(VotingDto votingDto) {
        if (votingDto == null) {
            throw new IllegalArgumentException("VotingDto must not be null");
        }
        if (votingDto.getTopicId() == null) {
            throw new IllegalArgumentException("Topic ID must not be null");
        }

        List<Voter> voters = votingDto.getVotersId() == null ? List.of() :
                votingDto.getVotersId().stream()
                        .map(id -> voterRepository.findById(id)
                                .orElseThrow(() -> new VoterNotFound(id)))
                        .toList();

        return Voting.builder()
                .id(votingDto.getId())
                .isAccepted(votingDto.isAccepted())
                .topic(topicRepository.findById(votingDto.getTopicId())
                        .orElseThrow(() -> new TopicNotFound(votingDto.getTopicId())))
                .voters(voters)
                .build();
    }

    /**
     * Converts a persistent {@link Voting} entity into a {@link VotingDto} for external use, such as API responses.
     * Extracts the topic ID from the associated topic and collects all voter IDs from the database for that topic.
     * This ensures the DTO reflects the current state of voters associated with the voting.
     *
     * @param voting the entity to convert; must not be null and must have an associated topic
     * @return a fully populated {@link VotingDto} containing the voting ID, acceptance status, topic ID, and list of voter IDs
     * @throws IllegalArgumentException if the provided {@code voting} is null or has no associated topic
     */
    public VotingDto toDto(Voting voting) {
        if (voting == null) {
            throw new IllegalArgumentException("Voting must not be null");
        }
        if (voting.getTopic() == null) {
            throw new IllegalArgumentException("Voting must be associated with a topic");
        }

        List<Integer> voterIds = voterRepository.findAllByTopic_Id(voting.getTopic().getId()).stream()
                .map(Voter::getId)
                .toList();

        return VotingDto.builder()
                .id(voting.getId())
                .isAccepted(voting.isAccepted())
                .topicId(voting.getTopic().getId())
                .votersId(voterIds)
                .build();
    }
}