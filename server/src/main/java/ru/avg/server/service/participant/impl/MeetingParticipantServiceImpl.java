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
 * Implementation of the {@link MeetingParticipantService} interface.
 * This service manages meeting participants, including adding participants,
 * retrieving existing and potential participants, as well as removing participants from meetings.
 * When a participant is added to a meeting, it also triggers the creation of voting records
 * for each topic associated with the meeting.
 * <p>
 * This implementation ensures data isolation by restricting all operations to a specific
 * company and meeting, thereby guaranteeing access control.
 * </p>
 *
 * @author AVG
 * @see MeetingParticipantService
 * @see MeetingParticipant
 * @see MeetingParticipantDto
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MeetingParticipantServiceImpl implements MeetingParticipantService {

    /**
     * Mapper for converting between {@link MeetingParticipant} entities and {@link MeetingParticipantDto} DTOs.
     * This dependency is injected by Spring and is used throughout the service to transform
     * data between the persistence layer and the API layer.
     *
     * @see MeetingParticipantMapper
     * @see MeetingParticipant
     * @see MeetingParticipantDto
     */
    private final MeetingParticipantMapper meetingParticipantMapper;

    /**
     * Repository for managing persistence of {@link MeetingParticipant} entities.
     * This dependency is injected by Spring via constructor injection and provides
     * CRUD operations and custom queries for meeting participant records.
     *
     * @see MeetingParticipantRepository
     * @see MeetingParticipant
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Repository for managing topics within meetings; used to retrieve topics when creating voting records.
     * This dependency is injected by Spring and is primarily used in the {@link #save(Integer, Integer, List)}
     * method to fetch all topics associated with a meeting.
     *
     * @see TopicRepository
     * @see #save(Integer, Integer, List)
     */
    private final TopicRepository topicRepository;

    /**
     * Mapper for converting {@link TopicDto} to entity when creating voting records.
     * This dependency is injected by Spring and is used to transform topic DTOs
     * into entity objects before passing them to the voting service.
     *
     * @see TopicMapper
     * @see TopicDto
     */
    private final TopicMapper topicMapper;

    /**
     * Service for managing voting record creation when a participant is added to a meeting.
     * This dependency is injected by Spring and is used to initialize voting structures
     * for each topic associated with a meeting when a new participant is added.
     *
     * @see VotingService
     * @see #save(Integer, Integer, List)
     */
    private final VotingService votingService;

    /**
     * Service for managing participants based on meeting type; used to find potential participants.
     * This dependency is injected by Spring and provides methods to query participants
     * by meeting type, which is used in {@link #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)}
     * and {@link #findPotentialInNewMeeting(Integer, String, Integer, Integer)}.
     *
     * @see ParticipantService
     * @see #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)
     * @see #findPotentialInNewMeeting(Integer, String, Integer, Integer)
     */
    private final ParticipantService participantService;

    /**
     * Service for retrieving meeting details (such as type and company ID); used when managing participants.
     * This dependency is injected by Spring and is primarily used in {@link #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)}
     * to fetch meeting information for determining potential participants.
     *
     * @see MeetingService
     * @see #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)
     */
    private final MeetingService meetingService;

    /**
     * Utility component for verifying the existence of companies and meetings, as well as their correct association.
     * This dependency is injected by Spring and is used throughout the service to validate
     * that operations are performed only when the specified company and meeting exist and are properly linked.
     *
     * @see Verifier
     * @see #verifyCompanyAndMeeting(Integer, Integer)
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
            if (meetingParticipantRepository
                    .findByMeetingIdAndParticipantId(meetingId, participant.getParticipant().getId())
                    .isEmpty()) {
                MeetingParticipant meetingParticipant = meetingParticipantRepository
                        .save(meetingParticipantMapper.fromNewDto(participant));
                List<TopicDto> topics = topicRepository.findAllByMeetingId(meetingParticipant.getMeeting().getId())
                        .stream()
                        .map(topicMapper::toDto)
                        .toList();
                for (TopicDto topic : topics) {
                    votingService.save(topicMapper.fromDto(topic));
                }
            }
        }
        return meetingParticipantRepository.findAllByMeetingId(meetingId)
                .stream()
                .map(meetingParticipantMapper::toDto)
                .toList();
    }

    /**
     * Добавляет одного участника к встрече с проверкой принадлежности участника компании.
     * <p>
     * Этот метод сначала проверяет существование компании и встречи через {@link Verifier#verifyCompanyAndMeeting(Integer, Integer)},
     * затем проверяет, что участник принадлежит указанной компании через {@link Verifier#verifyParticipant(Integer, Integer, Integer)}.
     * После валидации создаётся новая запись о участнике встречи.
     * </p>
     *
     * @param companyId             ID компании, владеющей встречей; не должно быть {@code null} и должно существовать
     * @param meetingId             ID встречи, к которой будет добавлен участник; не должно быть {@code null} и должно существовать
     * @param meetingParticipantDto DTO участника для добавления; не должно быть {@code null}
     * @return сохранённый {@link MeetingParticipantDto} со сгенерированным ID
     * @throws CompanyNotFound            если указанная компания не существует
     * @throws MeetingNotFound            если указанная встреча не существует
     * @throws MeetingParticipantNotFound если участник не принадлежит указанной компании
     */
    @Override
    public MeetingParticipantDto save(Integer companyId, Integer meetingId, MeetingParticipantDto meetingParticipantDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);
        verifier.verifyParticipant(companyId, meetingId, meetingParticipantDto.getParticipant().getId());

        MeetingParticipant newMeetingParticipant = meetingParticipantRepository
                .save(meetingParticipantMapper.fromDto(meetingParticipantDto));
        return meetingParticipantMapper.toDto(newMeetingParticipant);
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

        List<MeetingParticipantDto> data = meetingParticipantRepository
                .findAllByMeetingId(meetingId, pageable).getContent()
                .stream().map(meetingParticipantMapper::toDto)
                .toList();
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
     * @return страница {@link MeetingParticipantDto}, представляющая доступных участников, еще не добавленных во встречу;
     * возвращает пустую страницу, если потенциальные участники отсутствуют
     * @throws CompanyNotFound     если указанная компания не существует
     * @throws MeetingNotFound     если указанная встреча не существует
     * @throws MeetingTypeNotFound если тип встречи недействителен или не поддерживается
     */
    @Override
    public Page<MeetingParticipantDto> findPotentialInCurrentMeeting(Integer companyId, Integer meetingId,
                                                                     Integer page, Integer limit) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        MeetingDto meetingDto = meetingService.findById(companyId, meetingId);

        // Get current participant IDs as a Set for O(1) lookup
        Set<Integer> currentParticipantIds = meetingParticipantRepository.findAllByMeetingId(meetingId)
                .stream()
                .map(mp -> mp.getParticipant().getId())
                .collect(Collectors.toSet());

        // Find meeting type
        MeetingType meetingType = Arrays
                .stream(MeetingType.values())
                .filter(mt -> mt.getTitle()
                        .equals(meetingDto.getMeetingType()))
                .findFirst()
                .orElseThrow(() -> new MeetingTypeNotFound(meetingDto.getMeetingType()));

        // Get potential participants and filter out those already in the meeting
        List<MeetingParticipantDto> allMeetingParticipants = participantService
                .findAllByMeetingType(meetingDto.getCompanyId(), meetingType)
                .stream()
                .filter(ParticipantDto::getIsActive)
                .map(meetingParticipantMapper::fromParticipantDto)
                .filter(dto -> !currentParticipantIds.contains(dto.getParticipant().getId()))
                .toList();

        return createPage(allMeetingParticipants, page, limit);
    }

    /**
     * Находит потенциальных участников для создания новой встречи указанного типа.
     * <p>
     * В отличие от {@link #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)}, этот метод не фильтрует
     * участников по текущей встрече, а возвращает всех активных участников, соответствующих указанному типу встречи.
     * </p>
     *
     * @param companyId   ID компании; используется для валидации и поиска участников
     * @param meetingType название типа встречи (из {@link MeetingType#getTitle()}); используется для фильтрации участников
     * @param page        номер страницы
     * @param limit       лимит элементов на странице
     * @return страница {@link MeetingParticipantDto}, представляющая доступных участников для указанного типа встречи;
     * возвращает пустую страницу, если потенциальные участники отсутствуют
     * @throws CompanyNotFound     если указанная компания не существует
     * @throws MeetingTypeNotFound если тип встречи недействителен или не поддерживается
     */
    @Override
    public Page<MeetingParticipantDto> findPotentialInNewMeeting(Integer companyId, String meetingType,
                                                                 Integer page, Integer limit) {
        verifier.verifyCompany(companyId);

        MeetingType newMeetingType = Arrays
                .stream(MeetingType.values())
                .filter(mt -> mt.getTitle()
                        .equals(meetingType))
                .findFirst()
                .orElseThrow(() -> new MeetingTypeNotFound(meetingType));

        List<MeetingParticipantDto> allMeetingParticipants = participantService
                .findAllByMeetingType(companyId, newMeetingType)
                .stream()
                .filter(ParticipantDto::getIsActive)
                .map(meetingParticipantMapper::fromParticipantDto)
                .toList();

        return createPage(allMeetingParticipants, page, limit);
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
     * @throws MeetingParticipantNotFound если участник с указанным ID не найден во встрече
     */
    @Override
    public MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return meetingParticipantRepository
                .findByMeetingIdAndParticipantId(meetingId, participantId)
                .map(meetingParticipantMapper::toDto)
                .orElse(null);
    }

    /**
     * Получает все встречи, в которых участвует конкретный участник.
     * <p>
     * Метод возвращает все записи о участии в встречах для указанного участника,
     * с валидацией принадлежности компании.
     * </p>
     *
     * @param companyId     ID компании; используется для валидации
     * @param participantId ID участника для поиска
     * @return список {@link MeetingParticipantDto}, представляющий все встречи участника;
     * возвращает пустой список, если участник не найден
     * @throws CompanyNotFound если указанная компания не существует
     */
    @Override
    public List<MeetingParticipantDto> findAllByParticipantId(Integer companyId, Integer participantId) {
        verifier.verifyCompany(companyId);

        return meetingParticipantRepository.findByParticipantId(participantId)
                .stream()
                .map(meetingParticipantMapper::toDto)
                .toList();
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

    /**
     * Удаляет всех участников, связанных с конкретной встречей.
     * <p>
     * Этот метод выполняет каскадное удаление всех записей {@link MeetingParticipant},
     * связанных с указанной встречей. Используется при удалении встречи или очистке
     * её состава участников.
     * </p>
     *
     * @param meetingId ID встречи, для которой необходимо удалить всех участников
     * @throws MeetingNotFound если указанная встреча не существует
     */
    @Override
    public void deleteByMeetingId(Integer meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
    }

    /**
     * Создаёт страницу результатов с поддержкой пагинации для списка участников.
     * <p>
     * Этот приватный метод инкапсулирует логику вычисления индексов начала и конца страницы,
     * извлечения под списка и создания объекта {@link PageImpl}. Используется в методах
     * {@link #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)} и
     * {@link #findPotentialInNewMeeting(Integer, String, Integer, Integer)} для устранения
     * дублирования кода пагинации.
     * </p>
     *
     * @param participants список всех участников для пагинации; не должен быть {@code null}
     * @param page         номер страницы (с нуля)
     * @param limit        максимальное количество элементов на странице
     * @return страница {@link MeetingParticipantDto} с элементами текущей страницы,
     * общим количеством элементов и параметрами пагинации
     */
    private Page<MeetingParticipantDto> createPage(List<MeetingParticipantDto> participants, Integer page, Integer limit) {
        // Get current page from all available participants
        Pageable pageRequest = PageRequest.of(page, limit);
        int start = Math.min(pageRequest.getPageNumber() * pageRequest.getPageSize(), participants.size());
        int end = Math.min(start + pageRequest.getPageSize(), participants.size());
        List<MeetingParticipantDto> pageContent = participants.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, participants.size());
    }
}