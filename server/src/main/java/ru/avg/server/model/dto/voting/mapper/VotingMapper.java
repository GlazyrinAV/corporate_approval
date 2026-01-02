package ru.avg.server.model.dto.voting.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.model.dto.voting.VotingDto;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.repository.voting.VoterRepository;

import java.util.List;

/**
 * Mapper component responsible for bidirectional conversion between {@link Voting} entities
 * and {@link VotingDto} data transfer objects.
 * <p>
 * This class handles the transformation of voting session data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of the association between voting sessions,
 * topics, and individual votes. The mapper validates referential integrity by ensuring the topic ID
 * and voter IDs in the DTO correspond to existing records during the {@code fromDto} operation.
 * </p>
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * It delegates data access to repositories to resolve relationships during mapping.
 * </p>
 *
 * @see Voting
 * @see VotingDto
 * @see Voter
 * @see TopicRepository
 * @see VoterRepository
 * @author AVG
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class VotingMapper {

    /**
     * Repository used to fetch topic entities by ID when mapping voting DTOs.
     * This dependency is injected by Spring and used to establish the relationship between
     * a voting session and its parent topic during the mapping process.
     * <p>
     * The repository is used to validate that the topic ID provided in the DTO
     * corresponds to an existing topic, throwing {@link TopicNotFound} if not found.
     * This ensures referential integrity at the mapping layer.
     * </p>
     */
    private final TopicRepository topicRepository;

    /**
     * Repository used to fetch voter entities by ID when mapping voting DTOs.
     * This dependency is injected by Spring and used to resolve individual votes
     * associated with a voting session.
     * <p>
     * The repository is used to validate that all voter IDs provided in the DTO
     * correspond to existing voter records, throwing {@link VoterNotFound} if any
     * referenced voter does not exist. This ensures data consistency during mapping.
     * </p>
     */
    private final VoterRepository voterRepository;

    /**
     * Converts a {@link VotingDto} object into a {@link Voting} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code topicId} from the DTO into a proper {@link ru.avg.server.model.topic.Topic} entity
     * by fetching it from the repository. It also resolves all voter IDs into actual {@link Voter} entities
     * by fetching them from the voter repository. If the {@code votersId} list is null, it defaults to an empty list.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input DTO is not null</li>
     *   <li>The topic ID in the DTO is not null</li>
     *   <li>The specified topic exists in the database</li>
     *   <li>All specified voter IDs correspond to existing voter records</li>
     * </ul>
     * </p>
     *
     * @param votingDto the DTO containing voting data, must not be {@code null} and must have a non-null topicId
     * @return a fully constructed {@link Voting} entity with mapped values and resolved relationships
     * @throws IllegalArgumentException if {@code votingDto} is {@code null} or if {@code topicId} is null
     * @throws TopicNotFound            if the {@code topicId} in the DTO does not correspond to any existing topic
     * @throws VoterNotFound            if any of the voter IDs in the {@code votersId} list do not correspond to existing voter records
     * @see Voting#builder()
     * @see TopicRepository#findById(Object)
     * @see VoterRepository#findById(Object)
     * @see VotingDto#getVotersId()
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
     * Converts a {@link Voting} entity into a {@link VotingDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * extracting the topic ID from the associated topic entity. It also queries the database
     * to retrieve all voter IDs associated with the same topic, ensuring the DTO reflects
     * the current state of all votes for this voting session.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input entity is not null</li>
     *   <li>The voting session has an associated topic</li>
     * </ul>
     * </p>
     *
     * @param voting the entity containing voting data, must not be {@code null} and must have an associated topic
     * @return a fully constructed {@link VotingDto} with mapped values including the list of current voter IDs
     * @throws IllegalArgumentException if {@code voting} is {@code null} or has no associated topic
     * @see VotingDto#builder()
     * @see VoterRepository#findAllByTopic_Id(Integer)
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