package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.participant.ParticipantTypeNotFound;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.company.CompanyRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper for converting between {@link Participant} entities and {@link ParticipantDto} data transfer objects.
 * This class is responsible for bidirectional mapping with efficient lookup of {@link ParticipantType} by title.
 * It is designed as a Spring component to be autowired into services that require participant-related conversions.
 */
@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    private final CompanyRepository companyRepository;

    /**
     * Pre-built immutable map for O(1) lookup of {@link ParticipantType} by its title.
     * Initialized at startup to avoid repeated stream operations during mapping.
     */
    private static final Map<String, ParticipantType> PARTICIPANT_TYPE_MAP = Arrays.stream(ParticipantType.values())
            .collect(Collectors.toMap(ParticipantType::getTitle, Function.identity()));

    /**
     * Converts a {@link ParticipantDto} to a {@link Participant} entity.
     * Uses a pre-built map for efficient {@link ParticipantType} resolution.
     *
     * @param participantDto the DTO to convert; must not be null
     * @return the fully populated {@link Participant} entity
     * @throws IllegalArgumentException if the provided {@code participantDto} is null
     * @throws ParticipantTypeNotFound  if no participant type matches the given title
     * @throws CompanyNotFound          if no company exists with the given company ID
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
     * Converts a {@link Participant} entity to a {@link ParticipantDto}.
     *
     * @param participant the entity to convert; must not be null
     * @return the corresponding {@link ParticipantDto} with all relevant fields populated
     * @throws IllegalArgumentException if the provided {@code participant} is null
     *                                  or if it has no associated company
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