package ru.avg.server.repository.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.voting.Voting;

import java.util.Optional;

/**
 * Repository interface for managing {@link Voting} entities.
 * Provides CRUD operations and custom query methods for voting session data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval and management
 * of voting sessions by topic.
 * </p>
 */
@Repository
public interface VotingRepository extends JpaRepository<Voting, Integer> {

    /**
     * Finds a voting session by its associated topic ID.
     *
     * @param topicId the ID of the topic; must not be null
     * @return an {@link Optional} containing the voting session if found, or empty otherwise
     */
    Optional<Voting> findByTopicId(@Param("topicId") Integer topicId);

    /**
     * Deletes a voting session by its associated topic ID.
     * <p>
     * This method is annotated with {@link Modifying} to indicate that it changes the data state.
     * It uses a custom JPQL query to ensure deletion is performed correctly within a transactional context.
     * </p>
     *
     * @param topicId the ID of the topic whose voting session should be deleted
     * @return the number of deleted entities (0 or 1)
     */
    @Modifying
    @Query("DELETE FROM Voting v WHERE v.topic.id = :topicId")
    int deleteByTopicId(@Param("topicId") Integer topicId);
}