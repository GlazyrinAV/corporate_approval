package ru.avg.server.repository.participant;


import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.avg.server.model.participant.MeetingParticipant;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link MeetingParticipant} entities.
 * Provides CRUD operations and custom query methods for accessing data
 * about the association between participants and meetings.
 * <p>
 * This interface extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval and management
 * of meeting participants.
 * </p>
 *
 * @author AVG
 * @see MeetingParticipant
 * @see JpaRepository
 * @since 1.0
 */
@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Integer> {

    /**
     * Saves a list of meeting participants.
     * <p>
     * Note: This method overrides the standard save behavior for working with a list.
     * It is recommended to use {@link JpaRepository#saveAll(Iterable)} if special logic is not required.
     * </p>
     *
     * @param meetingParticipants list of participants to save; must not be {@code null}
     * @return the saved list of meeting participants
     * @since 1.0
     */
    List<MeetingParticipant> save(List<MeetingParticipant> meetingParticipants);

    /**
     * Сохраняет одного участника встречи.
     * <p>
     * Этот метод переопределяет стандартное поведение {@link JpaRepository#save(Object)} для работы
     * с одиночной сущностью. Использует аннотацию {@code @NonNull} для гарантии неизменности параметра.
     * </p>
     *
     * @param meetingParticipant участник встречи для сохранения; не должен быть {@code null}
     * @return сохранённая сущность {@link MeetingParticipant}
     * @since 1.0
     */
    MeetingParticipant save(@NonNull MeetingParticipant meetingParticipant);

    /**
     * Finds all participants associated with a specific meeting.
     *
     * @param meetingId ID of the meeting; must not be {@code null}
     * @return list of meeting participants, never {@code null}
     * @since 1.0
     */
    List<MeetingParticipant> findAllByMeetingId(Integer meetingId);

    /**
     * Finds all participants of a specific meeting with pagination support.
     *
     * @param meetingId ID of the meeting; must not be {@code null}
     * @param pageable pagination information
     * @return a page of meeting participants
     * @since 1.0
     */
    Page<MeetingParticipant> findAllByMeetingId(Integer meetingId, Pageable pageable);

    /**
     * Finds a specific meeting participant by meeting ID and participant ID.
     *
     * @param meetingId     ID of the meeting; must not be {@code null}
     * @param participantId ID of the participant; must not be {@code null}
     * @return {@link Optional} containing the found participant, or empty if not found
     * @since 1.0
     */
    Optional<MeetingParticipant> findByMeetingIdAndParticipantId(Integer meetingId, Integer participantId);

    /**
     * Finds all meetings in which a specific participant participates.
     *
     * @param participantId ID of the participant; must not be {@code null}
     * @return list of meeting participants, never {@code null}
     * @since 1.0
     */
    List<MeetingParticipant> findByParticipantId(Integer participantId);

    /**
     * Deletes all participants associated with a specific meeting.
     * <p>
     * This method performs a cascade delete of all {@link MeetingParticipant} entities
     * associated with the given meeting ID.
     * </p>
     *
     * @param meetingId ID of the meeting for which to delete all participants
     * @return the number of entities deleted
     * @since 1.0
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("DELETE FROM MeetingParticipant mp WHERE mp.meeting.id = :meetingId")
    int deleteByMeetingId(@Param("meetingId") Integer meetingId);
}