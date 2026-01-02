package ru.avg.server.model.dto.participant.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.participant.ParticipantTypeNotFound;
import ru.avg.server.model.dto.participant.NewParticipantDto;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.company.CompanyRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper component responsible for converting {@link NewParticipantDto} to a {@link Participant} entity.
 * <p>
 * This class handles the transformation of data from the DTO layer (used in API requests)
 * into the domain model layer (used internally by the application). It resolves the participant type
 * from a string to the corresponding enum value and fetches the associated company from the database.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Converts a {@link NewParticipantDto} object into a {@link Participant} entity</li>
 *   <li>Maps the string-based participant type to the corresponding {@link ParticipantType} enum</li>
 *   <li>Resolves the company reference by fetching it from {@link CompanyRepository}</li>
 *   <li>Validates input data and throws appropriate exceptions for invalid values</li>
 * </ul>
 *
 * <p>The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.</p>
 *
 * @see NewParticipantDto
 * @see Participant
 * @see ParticipantType
 * @see CompanyRepository
 * @author AVG
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NewParticipantMapper {

    /**
     * Repository used to fetch company entities by ID when mapping participant DTOs.
     * This dependency is injected by Spring and used to establish the relationship
     * between a participant and their associated company during the mapping process.
     */
    private final CompanyRepository companyRepository;

    /**
     * A static map that associates participant type titles (in Russian) with their corresponding
     * {@link ParticipantType} enum values. This map enables efficient lookup when converting
     * the string-based type from the DTO to the enum type in the entity.
     * <p>
     * The map is built from all values of the {@link ParticipantType} enum using the {@code title}
     * field as the key. It allows for fast resolution of participant types during the mapping process.
     * </p>
     */
    private static final Map<String, ParticipantType> PARTICIPANT_TYPE_MAP = Arrays.stream(ParticipantType.values())
            .collect(Collectors.toMap(ParticipantType::getTitle, Function.identity()));

    /**
     * Converts a {@link NewParticipantDto} object into a {@link Participant} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code type} string into a proper {@link ParticipantType} enum value
     * and fetching the associated company from the database.
     * </p>
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the input DTO is not null</li>
     *   <li>Resolves the participant type from the DTO's type string</li>
     *   <li>Fetches the associated company by ID from the repository</li>
     *   <li>Builds and returns a new Participant entity with all mapped values</li>
     * </ol>
     *
     * @param newParticipantDto the DTO containing participant data, must not be {@code null}
     * @return a fully constructed {@link Participant} entity with mapped values
     * @throws IllegalArgumentException if {@code newParticipantDto} is {@code null}
     * @throws ParticipantTypeNotFound if the {@code type} in the DTO does not correspond to any known participant type
     * @throws CompanyNotFound if the {@code companyId} in the DTO does not correspond to any existing company
     *
     * @see ParticipantType
     * @see CompanyRepository#findById(Object)
     */
    public Participant fromDto(NewParticipantDto newParticipantDto) {
        if (newParticipantDto == null) {
            throw new IllegalArgumentException("NewParticipantDto must not be null");
        }

        ParticipantType type = PARTICIPANT_TYPE_MAP.get(newParticipantDto.getType());
        if (type == null) {
            throw new ParticipantTypeNotFound(newParticipantDto.getType());
        }

        return Participant.builder()
                .name(newParticipantDto.getName())
                .share(newParticipantDto.getShare())
                .type(type)
                .company(companyRepository.findById(newParticipantDto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(newParticipantDto.getCompanyId())))
                .isActive(newParticipantDto.getIsActive())
                .build();
    }
}