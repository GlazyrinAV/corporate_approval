package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.meeting.MeetingRepository;

/**
 * Mapper class responsible for converting between {@link MeetingParticipant} entities and their corresponding DTOs.
 * Provides methods to map from DTO to entity and vice versa, as well as creating a participant DTO from an existing participant.
 * This class is a Spring component and is intended to be injected and used by services that require such mappings.
 */
@Component
@RequiredArgsConstructor
public class MeetingParticipantMapper {

    private final MeetingRepository meetingRepository;
    private final ParticipantMapper participantMapper;

    /**
     * Converts a {@link MeetingParticipantDto} to a {@link MeetingParticipant} entity.
     * If the {@code isPresent} field in the DTO is null, it defaults to {@code false}.
     *
     * @param dto the DTO to convert; must not be null
     * @return the mapped {@link MeetingParticipant} entity
     * @throws IllegalArgumentException if the provided DTO is null
     * @throws MeetingNotFound if no meeting exists with the given meeting ID
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
                .participant(participantMapper.fromDto(dto.getParticipant()))
                .build();
    }

    /**
     * Converts a {@link MeetingParticipant} entity to a {@link MeetingParticipantDto}.
     * Handles null safety for nested objects, particularly the meeting reference.
     *
     * @param participant the entity to convert; must not be null
     * @return the mapped {@link MeetingParticipantDto}
     * @throws IllegalArgumentException if the provided entity is null
     */
    public MeetingParticipantDto toDto(MeetingParticipant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        return MeetingParticipantDto.builder()
                .id(participant.getId())
                .meetingId(participant.getMeeting() != null ? participant.getMeeting().getId() : null)
                .isPresent(participant.isPresent())
                .participant(participantMapper.toDto(participant.getParticipant()))
                .build();
    }

    /**
     * Creates a new {@link MeetingParticipantDto} from a {@link ParticipantDto}, setting default values.
     * This is typically used when adding an existing participant to a meeting, with initial attendance status set to absent.
     *
     * @param participant the participant to wrap; must not be null
     * @return a new {@link MeetingParticipantDto} with {@code isPresent} set to {@code false}
     * @throws IllegalArgumentException if the provided participant is null
     */
    public MeetingParticipantDto fromParticipantDto(ParticipantDto participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant must not be null");
        }

        return MeetingParticipantDto.builder()
                .id(participant.getId())
                .participant(participant)
                .isPresent(false)
                .build();
    }
}