package ru.avg.server.repository.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.voting.Voter;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Voter} entities.
 * Provides CRUD operations and custom query methods for voting-related data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval of voters
 * by topic, voting session, or participant.
 * </p>
 */
@Repository
public interface VoterRepository extends JpaRepository<Voter, Integer> {

    /**
     * Retrieves all voters associated with a specific topic.
     *
     * @param topicId the ID of the topic; must not be null
     * @return a list of voters, never null
     */
    List<Voter> findAllByTopic_Id(@Param("topicId") Integer topicId);

    /**
     * Retrieves all voters associated with a specific voting session.
     *
     * @param votingId the ID of the voting session; must not be null
     * @return a list of voters, never null
     */
    List<Voter> findAllByVoting_Id(@Param("votingId") Integer votingId);

    /**
     * Finds a voter by participant ID and topic ID.
     * Useful for checking if a specific participant has voted on a topic.
     *
     * @param participantId the ID of the meeting participant
     * @param topicId the ID of the topic
     * @return an {@link Optional} containing the voter if found, or empty otherwise
     */
    @Query("SELECT v FROM Voter v WHERE v.participant.id = :participantId AND v.topic.id = :topicId")
    Optional<Voter> findByParticipantIdAndTopicId(
            @Param("participantId") Integer participantId,
            @Param("topicId") Integer topicId);

    /**
     * Counts the number of voters who voted 'FOR' on a specific topic.
     *
     * @param topicId the ID of the topic
     * @return the count of voters who voted 'FOR'
     */
    @Query("SELECT COUNT(v) FROM Voter v WHERE v.topic.id = :topicId AND v.vote.title = 'ЗА'")
    long countVotesForByTopicId(@Param("topicId") Integer topicId);

    /**
     * Counts the number of voters who voted 'AGAINST' on a specific topic.
     *
     * @param topicId the ID of the topic
     * @return the count of voters who voted 'AGAINST'
     */
    @Query("SELECT COUNT(v) FROM Voter v WHERE v.topic.id = :topicId AND v.vote.title = 'ПРОТИВ'")
    long countVotesAgainstByTopicId(@Param("topicId") Integer topicId);
}