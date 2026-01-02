package ru.avg.server.model.dto.meeting.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
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
 * Mapper component responsible for converting {@link NewMeetingDto} to {@link Meeting} entity.
 * Handles resolution of related entities such as company, chairman, and secretary by their IDs,
 * and maps string-based meeting type to the corresponding enum value.
 *
 * <p>This mapper enforces business constraints during conversion:
 * <ul>
 *   <li>Validates that the company exists</li>
 *   <li>Validates that the meeting type is supported</li>
 *   <li>Validates that chairman and secretary (if provided) exist</li>
 * </ul>
 * </p>
 *
 * @see NewMeetingDto
 * @see Meeting
 * @author AVG
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NewMeetingMapper {

    /**
     * Repository used to fetch company entities by ID when mapping meeting DTOs.
     * This dependency is injected by Spring and used to establish the relationship
     * between a meeting and its associated company during the mapping process.
     */
    private final CompanyRepository companyRepository;

    /**
     * Repository used to fetch participant entities by ID when mapping meeting DTOs.
     * This dependency is injected by Spring and used to assign participants to roles
     * such as chairman and secretary during the mapping process.
     */
    private final ParticipantRepository participantRepository;

    /**
     * A static map that associates meeting type titles (in Russian) with their corresponding
     * {@link MeetingType} enum values. This map enables efficient O(1) lookup when converting
     * the string-based type from the DTO to the enum type in the entity.
     * <p>
     * The map is built from all values of the {@link MeetingType} enum using the {@code title}
     * field as the key. It is initialized at startup to avoid repeated stream operations
     * during runtime mapping and ensures thread-safe access.
     * </p>
     */
    private static final Map<String, MeetingType> MEETING_TYPE_MAP = Arrays.stream(MeetingType.values())
            .collect(Collectors.toMap(MeetingType::getTitle, Function.identity()));

    /**
     * Converts a {@link NewMeetingDto} object into a {@link Meeting} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code type} string into a proper {@link MeetingType} enum value,
     * and fetching the associated company and participants (chairman, secretary) from their respective repositories.
     * </p>
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the input DTO is not null</li>
     *   <li>Resolves the meeting type from the DTO's type string using the pre-built map</li>
     *   <li>Fetches the associated company by ID from the repository</li>
     *   <li>Optionally fetches the chairman and secretary participants by ID if their IDs are present</li>
     *   <li>Builds and returns a new Meeting entity with all mapped values</li>
     * </ol>
     *
     * @param dto the DTO containing meeting data, must not be {@code null}
     * @return a fully constructed {@link Meeting} entity with mapped values
     * @throws IllegalArgumentException if {@code dto} is {@code null}
     * @throws MeetingTypeNotFound if the {@code type} in the DTO does not correspond to any known meeting type
     * @throws CompanyNotFound if the {@code companyId} in the DTO does not correspond to any existing company
     * @throws ParticipantNotFound if {@code chairmanId} or {@code secretaryId} in the DTO does not correspond to any existing participant
     *
     * @see MeetingType
     * @see CompanyRepository#findById(Object)
     * @see ParticipantRepository#findById(Object)
     */
    public Meeting fromDto(NewMeetingDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("NewMeetingDto must not be null");
        }

        // Resolve chairman if specified
        Participant chairman = dto.getChairmanId() != null
                ? participantRepository.findById(dto.getChairmanId())
                .orElseThrow(() -> new ParticipantNotFound(dto.getChairmanId()))
                : null;

        // Resolve secretary if specified
        Participant secretary = dto.getSecretaryId() != null
                ? participantRepository.findById(dto.getSecretaryId())
                .orElseThrow(() -> new ParticipantNotFound(dto.getSecretaryId()))
                : null;

        // Map meeting type from string to enum
        MeetingType meetingType = MEETING_TYPE_MAP.get(dto.getType());
        if (meetingType == null) {
            throw new MeetingTypeNotFound(dto.getType());
        }

        // Build and return the entity
        return Meeting.builder()
                .company(companyRepository.findById(dto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(dto.getCompanyId())))
                .type(meetingType)
                .date(dto.getDate())
                .address(dto.getAddress())
                .chairman(chairman)
                .secretary(secretary)
                .build();
    }
}