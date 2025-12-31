package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.participant.ParticipantRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link Meeting} entities and their corresponding DTOs.
 * This class is a Spring component and is intended to be injected and used by services that require such mappings.
 * It provides methods to map from DTO to entity and vice versa, ensuring proper handling of associations
 * and validation through exceptions when related entities are not found.
 */
@Component
@RequiredArgsConstructor
public class MeetingMapper {

    private final CompanyRepository companyRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Pre-built map for fast lookup of {@link MeetingType} by its title.
     * This map is initialized at class loading time and enables O(1) performance
     * when resolving a meeting type from a DTO, improving efficiency over linear search.
     */
    private static final Map<String, MeetingType> MEETING_TYPE_MAP = Arrays.stream(MeetingType.values())
            .collect(Collectors.toMap(MeetingType::getTitle, Function.identity()));

    /**
     * Converts a {@link MeetingDto} to a {@link Meeting} entity.
     * The conversion includes resolving the company, chairman, secretary, and meeting type.
     * If the meeting type title does not match any known type, a {@link MeetingTypeNotFound} exception is thrown.
     *
     * @param dto the DTO to convert; must not be null
     * @return the fully populated {@link Meeting} entity
     * @throws CompanyNotFound        if no company exists with the given company ID
     * @throws ParticipantNotFound    if no participant exists with the given chairman or secretary ID
     * @throws MeetingTypeNotFound    if the provided meeting type title does not correspond to any known type
     */
    public Meeting fromDto(MeetingDto dto) {
        Participant chairman = dto.getChairmanId() != null
                ? participantRepository.findById(dto.getChairmanId())
                .orElseThrow(() -> new ParticipantNotFound(dto.getChairmanId()))
                : null;

        Participant secretary = dto.getSecretaryId() != null
                ? participantRepository.findById(dto.getSecretaryId())
                .orElseThrow(() -> new ParticipantNotFound(dto.getSecretaryId()))
                : null;

        MeetingType meetingType = MEETING_TYPE_MAP.get(dto.getType());
        if (meetingType == null) {
            throw new MeetingTypeNotFound(dto.getType());
        }

        return Meeting.builder()
                .id(dto.getId())
                .company(companyRepository.findById(dto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(dto.getCompanyId())))
                .type(meetingType)
                .date(dto.getDate())
                .address(dto.getAddress())
                .chairman(chairman)
                .secretary(secretary)
                .build();
    }

    /**
     * Converts a {@link Meeting} entity to a {@link MeetingDto}.
     * The conversion includes extracting IDs from associated entities (company, chairman, secretary)
     * and mapping the meeting type to its title string.
     *
     * @param meeting the entity to convert; must not be null
     * @return the corresponding {@link MeetingDto} with all relevant fields populated
     */
    public MeetingDto toDto(Meeting meeting) {
        Integer chairmanId = meeting.getChairman() != null ? meeting.getChairman().getId() : null;
        Integer secretaryId = meeting.getSecretary() != null ? meeting.getSecretary().getId() : null;

        return MeetingDto.builder()
                .id(meeting.getId())
                .companyId(meeting.getCompany().getId())
                .type(meeting.getType().getTitle())
                .date(meeting.getDate())
                .address(meeting.getAddress())
                .chairmanId(chairmanId)
                .secretaryId(secretaryId)
                .build();
    }
}