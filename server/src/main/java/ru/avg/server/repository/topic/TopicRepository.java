package ru.avg.server.repository.topic;

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
     * Retrieves all topics associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting; must not be null
     * @return a list of topics, never null
     */
    List<Topic> findAllByMeeting_Id(@Param("meetingId") Integer meetingId);

    /**
     * Finds a topic by its ID and associated meeting ID.
     * Useful for validating ownership or access control.
     *
     * @param id the ID of the topic
     * @param meetingId the ID of the meeting
     * @return an {@link Optional} containing the topic if found and belongs to the meeting, or empty otherwise
     */
    @Query("SELECT t FROM Topic t WHERE t.id = :id AND t.meeting.id = :meetingId")
    Optional<Topic> findByIdAndMeetingId(@Param("id") Integer id, @Param("meetingId") Integer meetingId);

    /**
     * Counts the number of topics associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting
     * @return the number of topics in the meeting
     */
    long countByMeeting_Id(@Param("meetingId") Integer meetingId);
}