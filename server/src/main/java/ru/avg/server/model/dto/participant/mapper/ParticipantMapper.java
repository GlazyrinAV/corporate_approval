package ru.avg.server.model.dto.participant.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.participant.ParticipantTypeNotFound;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.company.CompanyRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper component responsible for bidirectional conversion between {@link Participant} entities
 * and {@link ParticipantDto} data transfer objects.
 * <p>
 * This class provides methods to transform data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of complex attributes such as
 * {@link ParticipantType} and company relationships. It uses a static map for efficient
 * lookup of participant types by their Russian title, avoiding repeated stream operations.
 * </p>
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * </p>
 *
 * @author AVG
 * @see Participant
 * @see ParticipantDto
 * @see ParticipantType
 * @see CompanyRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    /**
     * Repository used to fetch company entities by ID when mapping participant DTOs.
     * This dependency is injected by Spring and used to establish the relationship
     * between a participant and their associated company during the mapping process.
     */
    private final CompanyRepository companyRepository;

    /**
     * A static map that associates participant type titles (in Russian) with their corresponding
     * {@link ParticipantType} enum values. This map enables efficient O(1) lookup when converting
     * the string-based type from the DTO to the enum type in the entity.
     * <p>
     * The map is built from all values of the {@link ParticipantType} enum using the {@code title}
     * field as the key. It is initialized at startup to avoid repeated stream operations
     * during runtime mapping and ensures thread-safe access.
     * </p>
     */
    private static final Map<String, ParticipantType> PARTICIPANT_TYPE_MAP = Arrays.stream(ParticipantType.values())
            .collect(Collectors.toMap(ParticipantType::getTitle, Function.identity()));

    /**
     * Converts a {@link ParticipantDto} object into a {@link Participant} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code type} string into a proper {@link ParticipantType} enum value
     * and fetching the associated company from the database.
     * </p>
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the input DTO is not null</li>
     *   <li>Resolves the participant type from the DTO's type string using the pre-built map</li>
     *   <li>Fetches the associated company by ID from the repository</li>
     *   <li>Builds and returns a new Participant entity with all mapped values, including ID</li>
     * </ol>
     *
     * @param participantDto the DTO containing participant data, must not be {@code null}
     * @return a fully constructed {@link Participant} entity with mapped values
     * @throws IllegalArgumentException if {@code participantDto} is {@code null}
     * @throws ParticipantTypeNotFound  if the {@code type} in the DTO does not correspond to any known participant type
     * @throws CompanyNotFound          if the {@code companyId} in the DTO does not correspond to any existing company
     * @see ParticipantType
     * @see CompanyRepository#findById(Object)
     */
    public Participant fromDto(ParticipantDto participantDto) {
        if (participantDto == null) {
            throw new IllegalArgumentException("ParticipantDto must not be null");
        }

        ParticipantType type = PARTICIPANT_TYPE_MAP.get(participantDto.getType());
        if (type == null) {
            throw new ParticipantTypeNotFound(participantDto.getType());
        }

        return Participant.builder()
                .id(participantDto.getId())
                .name(participantDto.getName())
                .share(participantDto.getShare())
                .type(type)
                .company(companyRepository.findById(participantDto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(participantDto.getCompanyId())))
                .isActive(participantDto.getIsActive())
                .build();
    }

    /**
     * Converts a {@link Participant} entity into a {@link ParticipantDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * converting the {@link ParticipantType} enum back to its Russian title string and
     * extracting the company ID for association.
     * </p>
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the input entity is not null</li>
     *   <li>Ensures the participant is associated with a company</li>
     *   <li>Builds and returns a new ParticipantDto with all mapped values</li>
     * </ol>
     *
     * @param participant the entity containing participant data, must not be {@code null}
     *                    and must have an associated company
     * @return a fully constructed {@link ParticipantDto} with mapped values
     * @throws IllegalArgumentException if {@code participant} is {@code null}
     *                                  or if it has no associated company
     * @see ParticipantType#getTitle()
     */
    public ParticipantDto toDto(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant must not be null");
        }
        if (participant.getCompany() == null) {
            throw new IllegalArgumentException("Participant must be associated with a company");
        }

        return ParticipantDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .share(participant.getShare())
                .companyId(participant.getCompany().getId())
                .type(participant.getType().getTitle())
                .isActive(participant.getIsActive())
                .build();
    }
}