package ru.avg.server.repository.topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.topic.Topic;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Topic} entities.
 * Provides CRUD operations and custom query methods for topic-related data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval of topics
 * by meeting or other attributes.
 * </p>
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

    /**
     * Retrieves a paginated list of topics associated with a specific meeting,
     * sorted by topic title in ascending order.
     *
     * @param meetingId the ID of the meeting; must not be {@code null}
     * @param pageable  the pagination information including page number and size; must not be {@code null}
     * @return a {@link Page} of {@link Topic} entities containing the topics for the requested page,
     * including full pagination metadata; never {@code null}
     */
    Page<Topic> findAllByMeetingIdOrderByTitle(@Param("meetingId") Integer meetingId, Pageable pageable);

    /**
     * Retrieves all topics associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting; must not be {@code null}
     * @return a {@link List} of {@link Topic} entities belonging to the specified meeting;
     * returns an empty list if no topics are found; never {@code null}
     */
    List<Topic> findAllByMeetingId(@Param("meetingId") Integer meetingId);

    /**
     * Finds a topic by its ID and associated meeting ID.
     * Useful for validating ownership or access control when retrieving a specific topic.
     *
     * @param id        the ID of the topic
     * @param meetingId the ID of the meeting to which the topic must belong
     * @return an {@link Optional} containing the topic if found and belongs to the specified meeting,
     * or {@code Optional.empty()} otherwise
     */
    Optional<Topic> findByIdAndMeetingId(@Param("id") Integer id, @Param("meetingId") Integer meetingId);

    /**
     * Counts the number of topics associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting
     * @return the number of topics linked to the given meeting; returns 0 if no topics exist
     */
    long countByMeeting_Id(@Param("meetingId") Integer meetingId);

    /**
     * Searches for topics within a specific meeting based on a search criteria.
     * The search is case-insensitive and matches the criteria anywhere within the topic title.
     *
     * @param meetingId the ID of the meeting to search within; must not be {@code null}
     * @param criteria  the search string to match against topic titles; must not be {@code null}
     * @param pageable  the pagination information for retrieving results in pages; must not be {@code null}
     * @return a {@link Page} of {@link Topic} entities matching the search criteria,
     * sorted by title in ascending order; never {@code null}
     */
    @Query("SELECT t FROM Topic AS t WHERE t.meeting.id = :mettingId AND " +
            "(lower(t.title) like lower(concat('%', :criteria, '%'))) ORDER BY t.title")
    Page<Topic> findByCriteria(@Param("meetingId") Integer meetingId, @Param("criteria") String criteria, Pageable pageable);
}