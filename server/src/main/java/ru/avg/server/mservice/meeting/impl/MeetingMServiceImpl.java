package ru.avg.server.mservice.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.participant.MeetingParticipantEqual;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.MeetingUpdateDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
import ru.avg.server.model.dto.meeting.mapper.MeetingMapper;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;
import ru.avg.server.model.dto.participant.NewMeetingParticipantDto;
import ru.avg.server.model.dto.participant.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.mservice.meeting.MeetingMService;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.utils.verifier.Verifier;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link MeetingMService} for managing meetings at the microservice layer.
 * Handles meeting creation and updates, including participant management, chairman assignment,
 * and secretary assignment.
 * <p>
 * This service orchestrates multiple lower-level services to ensure proper meeting setup,
 * including validation via {@link Verifier}, participant management via {@link MeetingParticipantService},
 * and meeting persistence via {@link ru.avg.server.service.meeting.MeetingService}.
 * </p>
 *
 * @author AVG
 * @see MeetingMService
 * @see Meeting
 * @see MeetingParticipant
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MeetingMServiceImpl implements MeetingMService {

    /**
     * Service for managing meeting entities and their persistence.
     */
    private final MeetingService meetingService;

    /**
     * Service for managing individual participants; used to fetch participant details during meeting setup.
     */
    private final ParticipantService participantService;

    /**
     * Service for managing meeting-participant associations; used to add and update participants in meetings.
     */
    private final MeetingParticipantService meetingParticipantService;

    /**
     * Mapper for converting {@link ParticipantDto} to {@link Participant} entities.
     */
    private final ParticipantMapper participantMapper;

    /**
     * Mapper for converting {@link MeetingDto} to {@link Meeting} entities.
     */
    private final MeetingMapper meetingMapper;

    /**
     * Mapper for converting between {@link MeetingParticipant} entities and {@link MeetingParticipantDto} DTOs.
     */
    private final MeetingParticipantMapper meetingParticipantMapper;

    /**
     * Utility component for verifying company and meeting existence and ownership.
     */
    private final Verifier verifier;

    /**
     * Creates a new meeting with participants, chairman, and secretary.
     * <p>
     * This method first creates the meeting entity via {@link MeetingService#save(Integer, NewMeetingDto)},
     * then adds all specified participants via {@link #updateParticipants(Integer, Meeting, List)},
     * and finally assigns the chairman and secretary roles via {@link #updateChairman(Integer, Meeting, Integer)}
     * and {@link #updateSecretary(Integer, Meeting, Integer)}. If the chairman and secretary are the same person,
     * a {@link ru.avg.server.exception.participant.MeetingParticipantEqual} exception is thrown.
     * </p>
     *
     * @param companyId     ID of the company owning the meeting; must exist
     * @param newMeetingDto DTO containing meeting details including participants, chairman, and secretary
     * @return saved {@link MeetingDto} with generated ID and assigned roles
     * @throws ru.avg.server.exception.participant.MeetingParticipantEqual if chairman and secretary are the same person
     */
    @Override
    public MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto) {

        MeetingDto meetingDto = meetingService.save(companyId, newMeetingDto);
        Meeting newMeeting = meetingMapper.fromDto(meetingDto);

        updateParticipants(companyId, newMeeting, newMeetingDto.getMeetingParticipants());

        if (newMeetingDto.getChairmanId() != null && newMeetingDto.getSecretaryId() != null &&
                newMeetingDto.getChairmanId().equals(newMeetingDto.getSecretaryId())) {
            throw new MeetingParticipantEqual(newMeetingDto.getChairmanId());
        } else {
            updateChairman(companyId, newMeeting, newMeetingDto.getChairmanId());

            updateSecretary(companyId, newMeeting, newMeetingDto.getSecretaryId());

            meetingService.update(newMeeting, newMeeting);
        }

        return meetingService.findById(companyId, newMeeting.getId());
    }

    /**
     * Updates an existing meeting with new details.
     * <p>
     * This method first verifies the company and meeting existence via {@link Verifier#verifyCompanyAndMeeting(Integer, Integer)},
     * then applies updates from the DTO. If the meeting type changes, all existing participants are cleared
     * via {@link ru.avg.server.service.participant.MeetingParticipantService#deleteByMeetingId(Integer)}.
     * The chairman and secretary are updated based on the provided IDs.
     * </p>
     *
     * @param companyId  ID of the company; must exist
     * @param meetingId  ID of the meeting to update; must exist
     * @param meetingDto DTO containing updated meeting details
     * @return updated {@link MeetingDto} with applied changes
     * @throws IllegalArgumentException if company or meeting not found
     */
    public MeetingDto update(Integer companyId, Integer meetingId, MeetingUpdateDto meetingDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        Meeting existingMeeting = meetingMapper.fromDto(meetingService.findById(companyId, meetingId));

        Meeting updateSource = meetingMapper.fromUpdateDto(meetingDto);
        updateSource.setId(existingMeeting.getId());

        // Only apply updates for non-null or non-blank fields
        if (Objects.nonNull(updateSource.getType()) && updateSource.getType() != existingMeeting.getType()) {
            // Clear participants when meeting type changes (different quorum/rules may apply)
            meetingParticipantService.deleteByMeetingId(meetingId);
        }

        updateChairman(companyId, existingMeeting, meetingDto.getChairmanId());

        updateSecretary(companyId, existingMeeting, meetingDto.getSecretaryId());

        return meetingService.update(existingMeeting, updateSource);
    }

    /**
     * Creates a new meeting-participant association for a given participant.
     * <p>
     * This method creates a {@link MeetingParticipant} entity with the specified meeting,
     * participant, and presence flag. The participant is fetched from the database using
     * the provided participant ID.
     * </p>
     *
     * @param meeting       the meeting to associate with
     * @param participantId ID of the participant to add
     * @param isPresent    presence flag for the participant
     * @return created {@link MeetingParticipantDto}
     */
    private MeetingParticipantDto createMeetingParticipant(Meeting meeting, Integer participantId, Boolean isPresent) {
        MeetingParticipant newMeetingParticipant = MeetingParticipant.builder()
                .meeting(meeting)
                .participant(participantMapper
                        .fromDto(participantService.findById(meeting.getCompany().getId(), participantId)))
                .isPresent(isPresent)
                .build();

        return meetingParticipantMapper.toDto(newMeetingParticipant);
    }

    /**
     * Updates the list of participants for a meeting.
     * <p>
     * This method iterates through the provided list of {@link NewMeetingParticipantDto} objects
     * and adds each participant to the meeting via {@link MeetingParticipantService#save(Integer, Integer, MeetingParticipantDto)}.
     * If the input list is null, no operation is performed.
     * </p>
     *
     * @param companyId             ID of the company owning the meeting
     * @param meeting               the meeting to update participants for
     * @param newMeetingParticipants list of participants to add; may be null
     */
    private void updateParticipants(Integer companyId,
                                    Meeting meeting,
                                    List<NewMeetingParticipantDto> newMeetingParticipants) {
        if (newMeetingParticipants != null) {
            for (NewMeetingParticipantDto participant : newMeetingParticipants) {
                MeetingParticipantDto newParticipant =
                        createMeetingParticipant(meeting, participant.getParticipantId(), participant.getIsPresent());
                meetingParticipantService.save(companyId, meeting.getId(), newParticipant);
            }
        }
    }

    /**
     * Updates the chairman of a meeting.
     * <p>
     * If the chairman ID is provided (not null), this method checks if the participant already exists
     * in the meeting via {@link MeetingParticipantService#findByParticipantId(Integer, Integer, Integer)}.
     * If found, the meeting's chairman is set to that participant. Otherwise, a new participant record
     * is created and added to the meeting.
     * </p>
     *
     * @param companyId    ID of the company owning the meeting
     * @param meeting      the meeting to update the chairman for
     * @param chairmanId   ID of the participant to set as chairman; may be null
     */
    private void updateChairman(Integer companyId, Meeting meeting, Integer chairmanId) {
        if (chairmanId != null) {
            MeetingParticipantDto currentChairman = meetingParticipantService
                    .findByParticipantId(companyId, meeting.getId(), chairmanId);
            if (currentChairman != null) {
                meeting.setChairman(meetingParticipantMapper.fromDto(currentChairman));
            } else {
                MeetingParticipantDto newChairman = createMeetingParticipant(meeting, chairmanId, true);
                MeetingParticipantDto chairman = meetingParticipantService.save(companyId, meeting.getId(), newChairman);
                meeting.setChairman(meetingParticipantMapper.fromDto(chairman));
            }
        }
    }

    /**
     * Updates the secretary of a meeting.
     * <p>
     * If the secretary ID is provided (not null), this method checks if the participant already exists
     * in the meeting via {@link MeetingParticipantService#findByParticipantId(Integer, Integer, Integer)}.
     * If found, the meeting's secretary is set to that participant. Otherwise, a new participant record
     * is created and added to the meeting.
     * </p>
     *
     * @param companyId    ID of the company owning the meeting
     * @param meeting      the meeting to update the secretary for
     * @param secretaryId  ID of the participant to set as secretary; may be null
     */
    private void updateSecretary(Integer companyId, Meeting meeting, Integer secretaryId) {
        if (secretaryId != null) {
            MeetingParticipantDto currentSecretary = meetingParticipantService
                    .findByParticipantId(companyId, meeting.getId(), secretaryId);
            if (currentSecretary != null) {
                meeting.setSecretary(meetingParticipantMapper.fromDto(currentSecretary));
            } else {
                MeetingParticipantDto newSecretary = createMeetingParticipant(meeting, secretaryId, true);
                MeetingParticipantDto secretary = meetingParticipantService.save(companyId, meeting.getId(), newSecretary);
                meeting.setSecretary(meetingParticipantMapper.fromDto(secretary));
            }
        }
    }
}
