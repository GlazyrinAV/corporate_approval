package ru.avg.server.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.participant.MeetingParticipant;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link MeetingParticipant} entities.
 * Provides CRUD operations and custom query methods for participant-meeting association data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval and management
 * of meeting participants.
 * </p>
 */
@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Integer> {

    /**
     * Saves a list of meeting participants.
     * <p>
     * Note: This method overrides the default save behavior to accept a list.
     * Consider using {@link JpaRepository#saveAll(Iterable)} instead unless custom logic is required.
     * </p>
     *
     * @param meetingParticipants the list of participants to save; must not be null
     * @return the saved list of meeting participants
     */
    List<MeetingParticipant> save(List<MeetingParticipant> meetingParticipants);

    /**
     * Finds all participants associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting; must not be null
     * @return a list of meeting participants, never null
     *
     * @since 1.0
     */
    List<MeetingParticipant> findAllByMeetingId(Integer meetingId);

    /**
     * Finds a specific meeting participant by meeting and participant IDs.
     *
     * @param meetingId the ID of the meeting; must not be null
     * @param participantId the ID of the participant; must not be null
     * @return an {@link Optional} containing the found participant, or empty if not found
     *
     * @since 1.0
     */
    Optional<MeetingParticipant> findByMeetingIdAndParticipantId(Integer meetingId, Integer participantId);

    /**
     * Finds all meetings a specific participant is associated with.
     *
     * @param participantId the ID of the participant; must not be null
     * @return a list of meeting participants, never null
     *
     * @since 1.0
     */
    List<MeetingParticipant> findByParticipantId(Integer participantId);

    /**
     * Deletes all participants associated with a specific meeting.
     *
     * @param meetingId the ID of the meeting to clear
     * @return the number of deleted entities
     *
     * @since 1.0
     */
    @Modifying
    @Query("DELETE FROM MeetingParticipant mp WHERE mp.meeting.id = :meetingId")
    int deleteByMeetingId(@Param("meetingId") Integer meetingId);
}