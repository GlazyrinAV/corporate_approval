package ru.avg.server.repository.participant;


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
 * Интерфейс репозитория для управления сущностями {@link MeetingParticipant}.
 * Предоставляет операции CRUD и пользовательские методы запросов для доступа к данным
 * о связи участников и встреч.
 * <p>
 * Данный репозиторий расширяет {@link JpaRepository}, чтобы унаследовать стандартные операции с базой данных
 * и определяет дополнительные запросы, специфичные для бизнеса, для эффективного извлечения и управления
 * участниками встреч.
 * </p>
 */
@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Integer> {

    /**
     * Сохраняет список участников встречи.
     * <p>
     * Примечание: Этот метод переопределяет стандартное поведение сохранения для работы со списком.
     * Рекомендуется использовать {@link JpaRepository#saveAll(Iterable)}, если не требуется специальная логика.
     * </p>
     *
     * @param meetingParticipants список участников для сохранения; не должен быть null
     * @return сохраненный список участников встречи
     */
    List<MeetingParticipant> save(List<MeetingParticipant> meetingParticipants);

    /**
     * Находит всех участников, связанных с конкретной встречей.
     *
     * @param meetingId ID встречи; не должен быть null
     * @return список участников встречи, никогда не null
     * @since 1.0
     */
    List<MeetingParticipant> findAllByMeetingId(Integer meetingId);

    /**
     * Находит всех участников конкретной встречи с поддержкой пагинации.
     *
     * @param meetingId ID встречи; не должен быть null
     * @param pageable информация о пагинации
     * @return страница с участниками встречи
     * @since 1.0
     */
    Page<MeetingParticipant> findAllByMeetingId(Integer meetingId, Pageable pageable);

    /**
     * Находит конкретного участника встречи по ID встречи и ID участника.
     *
     * @param meetingId ID встречи; не должен быть null
     * @param participantId ID участника; не должен быть null
     * @return {@link Optional}, содержащий найденного участника, или пустой, если участник не найден
     * @since 1.0
     */
    Optional<MeetingParticipant> findByMeetingIdAndParticipantId(Integer meetingId, Integer participantId);

    /**
     * Находит все встречи, в которых участвует конкретный участник.
     *
     * @param participantId ID участника; не должен быть null
     * @return список участников встречи, никогда не null
     * @since 1.0
     */
    List<MeetingParticipant> findByParticipantId(Integer participantId);

    /**
     * Удаляет всех участников, связанных с конкретной встречей.
     *
     * @param meetingId ID встречи, для которой необходимо очистить участников
     * @return количество удаленных сущностей
     * @since 1.0
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("DELETE FROM MeetingParticipant mp WHERE mp.meeting.id = :meetingId")
    int deleteByMeetingId(@Param("meetingId") Integer meetingId);
}