package ru.avg.server.model.dto.participant.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.meeting.MeetingRepository;
import ru.avg.server.repository.participant.ParticipantRepository;

/**
 * Mapper component responsible for bidirectional conversion between {@link MeetingParticipant} entities
 * and {@link MeetingParticipantDto} data transfer objects.
 * <p>
 * This class handles the transformation of meeting-participant relationship data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of the association between participants and meetings,
 * including attendance status. The mapper validates referential integrity by ensuring the meeting ID
 * in the DTO corresponds to an existing meeting during the {@code fromDto} operation.
 * </p>
 * <p>
 * The mapper is registered as a Spring component using {@link Component} and receives
 * its dependencies via constructor injection, enabled by {@link RequiredArgsConstructor}.
 * It delegates participant mapping to {@link ParticipantMapper}, maintaining separation of concerns.
 * </p>
 *
 * @author AVG
 * @see MeetingParticipant
 * @see MeetingParticipantDto
 * @see ParticipantMapper
 * @see MeetingRepository
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class MeetingParticipantMapper {

    /**
     * Repository used to fetch meeting entities by ID when mapping meeting-participant DTOs.
     * This dependency is injected by Spring and used to establish the relationship between
     * a meeting-participant association and its parent meeting during the mapping process.
     * <p>
     * The repository is used to validate that the meeting ID provided in the DTO
     * corresponds to an existing meeting, throwing {@link MeetingNotFound} if not found.
     * This ensures referential integrity at the mapping layer.
     * </p>
     */
    private final MeetingRepository meetingRepository;

    /**
     * Repository used to fetch participant entities by ID when mapping meeting-participant DTOs.
     * This dependency is injected by Spring and used to validate that the participant ID
     * provided in the DTO corresponds to an existing participant, throwing
     * {@link ParticipantNotFound} if not found.
     */
    private final ParticipantRepository participantRepository;

    /**
     * Converts a {@link MeetingParticipantDto} object into a {@link MeetingParticipant} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code meetingId} from the DTO into a proper {@link ru.avg.server.model.meeting.Meeting} entity
     * by fetching it from the repository. The participant portion is mapped using {@link ParticipantMapper}.
     * If the {@code isPresent} field in the DTO is null, it defaults to {@code false}.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input DTO is not null</li>
     *   <li>The meeting ID in the DTO is not null</li>
     *   <li>The specified meeting exists in the database</li>
     *   <li>The participant in the DTO is not null</li>
     * </ul>
     * </p>
     *
     * @param dto the DTO containing meeting-participant data, must not be {@code null} and must have a non-null meetingId
     * @return a fully constructed {@link MeetingParticipant} entity with mapped values and resolved relationships
     * @throws IllegalArgumentException if {@code dto} is {@code null} or if {@code meetingId} is null
     * @throws MeetingNotFound          if the {@code meetingId} in the DTO does not correspond to any existing meeting
     * @see MeetingParticipant#builder()
     * @see MeetingRepository#findById(Object)
     * @see ParticipantMapper#fromDto(ParticipantDto)
     * @see MeetingParticipantDto#getIsPresent()
     */
    public MeetingParticipant fromDto(MeetingParticipantDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO must not be null");
        }
        if (dto.getIsPresent() == null) {
            dto.setIsPresent(false);
        }

        return MeetingParticipant.builder()
                .id(dto.getId())
                .isPresent(dto.getIsPresent())
                .meeting(meetingRepository.findById(dto.getMeetingId())
                        .orElseThrow(() -> new MeetingNotFound(dto.getMeetingId())))
                .participant(participantRepository.findById(dto.getParticipantId())
                        .orElseThrow(() -> new ParticipantNotFound(dto.getParticipantId())))
                .build();
    }

    /**
     * Converts a {@link MeetingParticipant} entity into a {@link MeetingParticipantDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * extracting the meeting ID from the associated meeting entity and including it in the DTO.
     * The participant portion is mapped using {@link ParticipantMapper}.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input entity is not null</li>
     *   <li>The meeting-participant association has a participant reference</li>
     * </ul>
     * </p>
     *
     * @param participant the entity containing meeting-participant data, must not be {@code null}
     * @return a fully constructed {@link MeetingParticipantDto} with mapped values
     * @throws IllegalArgumentException if {@code participant} is {@code null} or has no participant reference
     * @see MeetingParticipantDto#builder()
     * @see ParticipantMapper#toDto(ru.avg.server.model.participant.Participant)
     */
    public MeetingParticipantDto toDto(MeetingParticipant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        return MeetingParticipantDto.builder()
                .id(participant.getId())
                .meetingId(participant.getMeeting() != null ? participant.getMeeting().getId() : null)
                .isPresent(participant.isPresent())
                .participantId(participant.getId())
                .build();
    }

    /**
     * Creates a new {@link MeetingParticipantDto} from a {@link ParticipantDto}, setting default values.
     * <p>
     * This method is typically used when adding an existing participant to a meeting,
     * creating a new association with default values: attendance status set to absent ({@code false})
     * and meeting ID to be set later. The resulting DTO can then be used to establish
     * a new meeting-participant relationship.
     * </p>
     * <p>
     * The method performs validation to ensure:
     * <ul>
     *   <li>The input participant is not null</li>
     * </ul>
     * </p>
     *
     * @param participant the participant to wrap in a meeting-participant DTO, must not be {@code null}
     * @return a new {@link MeetingParticipantDto} with {@code isPresent} set to {@code false}
     * @throws IllegalArgumentException if {@code participant} is {@code null}
     * @see MeetingParticipantDto#builder()
     */
    public MeetingParticipantDto fromParticipantDto(ParticipantDto participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant must not be null");
        }

        return MeetingParticipantDto.builder()
                .participantId(participant.getId())
                .isPresent(false)
                .build();
    }
}