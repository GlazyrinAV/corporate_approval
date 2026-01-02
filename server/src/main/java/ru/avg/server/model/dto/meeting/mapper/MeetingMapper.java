package ru.avg.server.model.dto.meeting.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.meeting.MeetingDto;
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
 * Mapper component responsible for bidirectional conversion between {@link Meeting} entities
 * and {@link MeetingDto} data transfer objects.
 * <p>
 * This class provides methods to transform data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of complex attributes such as
 * {@link MeetingType}, {@link Participant} (chairman and secretary), and associated {@link ru.avg.server.model.company.Company}.
 * It uses static maps for efficient lookup of meeting types by their Russian title, avoiding repeated stream operations.
 * </p>
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * </p>
 *
 * @author AVG
 * @see Meeting
 * @see MeetingDto
 * @see MeetingType
 * @see Participant
 * @see CompanyRepository
 * @see ParticipantRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class MeetingMapper {

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
     * Converts a {@link MeetingDto} object into a {@link Meeting} entity.
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
     *   <li>Builds and returns a new Meeting entity with all mapped values, including ID</li>
     * </ol>
     *
     * @param dto the DTO containing meeting data, must not be {@code null}
     * @return a fully constructed {@link Meeting} entity with mapped values
     * @throws IllegalArgumentException if {@code dto} is {@code null}
     * @throws MeetingTypeNotFound      if the {@code type} in the DTO does not correspond to any known meeting type
     * @throws CompanyNotFound          if the {@code companyId} in the DTO does not correspond to any existing company
     * @throws ParticipantNotFound      if {@code chairmanId} or {@code secretaryId} in the DTO does not correspond to any existing participant
     * @see MeetingType
     * @see CompanyRepository#findById(Object)
     * @see ParticipantRepository#findById(Object)
     */
    public Meeting fromDto(MeetingDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("MeetingDto must not be null");
        }

        MeetingType type = MEETING_TYPE_MAP.get(dto.getType());
        if (type == null) {
            throw new MeetingTypeNotFound(dto.getType());
        }

        return Meeting.builder()
                .id(dto.getId())
                .type(type)
                .date(dto.getDate())
                .address(dto.getAddress())
                .company(companyRepository.findById(dto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(dto.getCompanyId())))
                .chairman(dto.getChairmanId() != null ?
                        participantRepository.findById(dto.getChairmanId())
                                .orElseThrow(() -> new ParticipantNotFound(dto.getChairmanId())) : null)
                .secretary(dto.getSecretaryId() != null ?
                        participantRepository.findById(dto.getSecretaryId())
                                .orElseThrow(() -> new ParticipantNotFound(dto.getSecretaryId())) : null)
                .build();
    }

    /**
     * Converts a {@link Meeting} entity into a {@link MeetingDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * converting the {@link MeetingType} enum back to its Russian title string and
     * extracting participant and company IDs for association.
     * </p>
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the input entity is not null</li>
     *   <li>Ensures the meeting is associated with a company</li>
     *   <li>Optionally extracts chairman and secretary participant IDs if they are assigned</li>
     *   <li>Builds and returns a new MeetingDto with all mapped values</li>
     * </ol>
     *
     * @param meeting the entity containing meeting data, must not be {@code null}
     *                and must have an associated company
     * @return a fully constructed {@link MeetingDto} with mapped values
     * @throws IllegalArgumentException if {@code meeting} is {@code null}
     *                                  or if it has no associated company
     * @see MeetingType#getTitle()
     */
    public MeetingDto toDto(Meeting meeting) {
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting must not be null");
        }
        if (meeting.getCompany() == null) {
            throw new IllegalArgumentException("Meeting must be associated with a company");
        }

        return MeetingDto.builder()
                .id(meeting.getId())
                .companyId(meeting.getCompany().getId())
                .type(meeting.getType().getTitle())
                .date(meeting.getDate())
                .address(meeting.getAddress())
                .chairmanId(meeting.getChairman() != null ? meeting.getChairman().getId() : null)
                .secretaryId(meeting.getSecretary() != null ? meeting.getSecretary().getId() : null)
                .build();
    }
}