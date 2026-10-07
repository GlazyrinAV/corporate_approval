package ru.avg.server.service.participant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.MeetingParticipantNotFound;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.dto.participant.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.topic.TopicDto;
import ru.avg.server.model.dto.topic.mapper.TopicMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.service.voting.VotingService;
import ru.avg.server.utils.verifier.Verifier;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link MeetingParticipantService}.
 * Данный сервис управляет участниками встреч, включая добавление участников,
 * получение существующих и потенциальных участников, а также удаление участников из встреч.
 * При добавлении участника к встрече также запускается создание голосований для каждой темы встречи.
 *
 * <p>Данная реализация обеспечивает изоляцию данных, ограничивая все операции конкретной
 * компанией и встречей, что гарантирует контроль доступа и поддержку мультиарендности.</p>
 *
 * @author AVG
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MeetingParticipantServiceImpl implements MeetingParticipantService {

    /**
     * Маппер для преобразования между сущностями {@link MeetingParticipant} и {@link MeetingParticipantDto}.
     */
    private final MeetingParticipantMapper meetingParticipantMapper;

    /**
     * Репозиторий для управления хранением сущностей {@link MeetingParticipant}.
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Репозиторий для управления темами внутри встречи; используется для получения тем при создании голосований.
     */
    private final TopicRepository topicRepository;

    /**
     * Маппер для преобразования {@link TopicDto} в сущность при создании голосований.
     */
    private final TopicMapper topicMapper;

    /**
     * Сервис для управления созданием голосований при добавлении участника к встрече.
     */
    private final VotingService votingService;

    /**
     * Сервис для управления участниками на основе типа встречи; используется для поиска потенциальных участников.
     */
    private final ParticipantService participantService;

    /**
     * Сервис для получения деталей встречи (например, типа и ID компании); используется при управлении участниками.
     */
    private final MeetingService meetingService;

    /**
     * Утилитарный компонент для проверки существования компании и встречи, а также их корректной связи.
     */
    private final Verifier verifier;

    /**
     * Добавляет нескольких участников к указанной встрече.
     * Для каждого добавленного участника этот метод запускает создание голосований для всех тем,
     * связанных со встречей, чтобы обеспечить инициализацию структуры голосований.
     *
     * @param companyId    ID компании, владеющей встречей; не должно быть null и должно существовать
     * @param meetingId    ID встречи, к которой будут добавлены участники; не должно быть null и должно существовать
     * @param participants список {@link MeetingParticipantDto}, представляющий добавляемых участников
     * @return список сохраненных {@link MeetingParticipantDto} со сгенерированными ID; никогда не null
     * @throws CompanyNotFound если указанная компания не существует
     * @throws MeetingNotFound если указанная встреча не существует
     */
    @Override
    public List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        meetingParticipantRepository.deleteByMeetingId(meetingId);

        for (MeetingParticipantDto participant : participants) {
            if (meetingParticipantRepository.findByMeetingIdAndParticipantId(meetingId, participant.getParticipant().getId())
                    .isEmpty()) {
                MeetingParticipant meetingParticipant = meetingParticipantRepository.save(meetingParticipantMapper.fromNewDto(participant));
                List<TopicDto> topics = topicRepository.findAllByMeetingId(meetingParticipant.getMeeting().getId())
                        .stream()
                        .map(topicMapper::toDto)
                        .toList();
                for (TopicDto topic : topics) {
                    votingService.save(topicMapper.fromDto(topic));
                }
            }
        }
        return participants;
    }

    /**
     * Получает всех участников, связанных с конкретной встречей.
     * Результат фильтруется, чтобы гарантировать возврат только участников, принадлежащих данной встрече.
     *
     * @param companyId ID компании; используется для валидации
     * @param meetingId ID встречи, для которой необходимо получить участников
     * @param page      номер страницы
     * @param limit     лимит элементов на странице
     * @return страница {@link MeetingParticipantDto}, представляющая всех участников встречи;
     * возвращает пустую страницу, если участники не найдены
     * @throws CompanyNotFound если указанная компания не существует
     * @throws MeetingNotFound если указанная встреча не существует
     */
    @Override
    public Page<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId, Integer page, Integer limit) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);
        Pageable pageable = PageRequest.of(page, limit);

        List<MeetingParticipantDto> data = meetingParticipantRepository.findAllByMeetingId(meetingId, pageable).getContent().stream().map(meetingParticipantMapper::toDto).toList();
        return new PageImpl<>(data, pageable, data.size());
    }

    /**
     * Находит потенциальных участников, которых можно добавить к встрече на основе типа встречи.
     * Исключает участников, которые уже являются частью встречи.
     *
     * @param companyId ID компании; используется для валидации и поиска участников
     * @param meetingId ID встречи; используется для определения текущих участников и типа встречи
     * @param page      номер страницы
     * @param limit     лимит элементов на странице
     * @return страница {@link MeetingParticipantDto}, представляющая доступных участников, еще не добавленных в встречу;
     * возвращает пустую страницу, если потенциальные участники отсутствуют
     * @throws CompanyNotFound     если указанная компания не существует
     * @throws MeetingNotFound     если указанная встреча не существует
     * @throws MeetingTypeNotFound если тип встречи недействителен или не поддерживается
     */
    @Override
    public Page<MeetingParticipantDto> findPotential(Integer companyId, Integer meetingId, Integer page, Integer limit) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        MeetingDto meetingDto = meetingService.findById(companyId, meetingId);

        // Get current participant IDs as a Set for O(1) lookup
        Set<Integer> currentParticipantIds = meetingParticipantRepository.findAllByMeetingId(meetingId).stream().map(mp -> mp.getParticipant().getId()).collect(Collectors.toSet());

        // Find meeting type
        MeetingType meetingType = Arrays.stream(MeetingType.values()).filter(mt -> mt.getTitle().equals(meetingDto.getMeetingType())).findFirst().orElseThrow(() -> new MeetingTypeNotFound(meetingDto.getMeetingType()));

        // Get potential participants and filter out those already in the meeting
        List<MeetingParticipantDto> allMeetingParticipants = participantService
                .findAllByMeetingType(meetingDto.getCompanyId(), meetingType)
                .stream()
                .filter(ParticipantDto::getIsActive)
                .map(meetingParticipantMapper::fromParticipantDto)
                .filter(dto -> !currentParticipantIds.contains(dto.getParticipant().getId()))
                .peek(dto -> dto.setMeetingId(meetingId))
                .toList();

        // Get current page from all available participants
        Pageable pageRequest = PageRequest.of(page, limit);
        int start = Math.min(pageRequest.getPageNumber() * pageRequest.getPageSize(), allMeetingParticipants.size());
        int end = Math.min(start + pageRequest.getPageSize(), allMeetingParticipants.size());
        List<MeetingParticipantDto> pageContent = allMeetingParticipants.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, allMeetingParticipants.size());
    }

    /**
     * Получает конкретного участника в рамках встречи по ID участника.
     *
     * @param companyId     ID компании; используется для валидации
     * @param meetingId     ID встречи; используется для ограничения контекста
     * @param participantId ID участника для получения
     * @return соответствующий {@link MeetingParticipantDto}, если найден
     * @throws CompanyNotFound            если указанная компания не существует
     * @throws MeetingNotFound            если указанная встреча не существует
     * @throws MeetingParticipantNotFound если участник с указанным ID не найден в встрече
     */
    @Override
    public MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return meetingParticipantRepository.findByMeetingIdAndParticipantId(meetingId, participantId).map(meetingParticipantMapper::toDto).orElseThrow(() -> new MeetingParticipantNotFound(participantId));
    }

    /**
     * Удаляет участника из встречи по ID связи встречи и участника.
     *
     * @param companyId            ID компании; используется для валидации
     * @param meetingId            ID встречи; используется для валидации
     * @param meetingParticipantId ID связи встречи и участника для удаления
     * @throws CompanyNotFound            если указанная компания не существует
     * @throws MeetingNotFound            если указанная встреча не существует
     * @throws MeetingParticipantNotFound если связь встречи и участника с указанным ID не найдена
     */
    @Override
    public void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        if (!meetingParticipantRepository.existsById(meetingParticipantId)) {
            throw new MeetingParticipantNotFound(meetingParticipantId);
        }
        meetingParticipantRepository.deleteById(meetingParticipantId);
    }
}