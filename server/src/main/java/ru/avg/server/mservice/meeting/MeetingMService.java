package ru.avg.server.mservice.meeting;

import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.MeetingUpdateDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;

/**
 * Service interface for managing meetings at the microservice layer.
 * Provides operations for creating and updating meetings, including
 * associated participants, chairmen, and secretaries.
 * <p>
 * This interface abstracts the business logic for meeting management,
 * delegating to lower-level services for data persistence and validation.
 * </p>
 *
 * @author AVG
 * @see MeetingDto
 * @see NewMeetingDto
 * @see MeetingUpdateDto
 * @since 1.0
 */
public interface MeetingMService {

    /**
     * Creates a new meeting with participants, chairman, and secretary.
     * <p>
     * This method creates the meeting entity, adds all specified participants,
     * and assigns the chairman and secretary roles. If the chairman and secretary
     * IDs are the same, a {@link ru.avg.server.exception.participant.MeetingParticipantEqual} exception is thrown.
     * </p>
     *
     * @param companyId     ID of the company owning the meeting; must exist
     * @param newMeetingDto DTO containing meeting details including participants, chairman, and secretary
     * @return saved {@link MeetingDto} with generated ID and assigned roles
     * @throws ru.avg.server.exception.participant.MeetingParticipantEqual if chairman and secretary are the same person
     */
    MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto);

    /**
     * Updates an existing meeting with new details.
     * <p>
     * This method verifies the company and meeting existence, applies updates from the DTO,
     * and handles participant reassignment when the meeting type changes. The chairman and secretary
     * are updated based on the provided IDs.
     * </p>
     *
     * @param companyId  ID of the company; must exist
     * @param meetingId  ID of the meeting to update; must exist
     * @param meetingDto DTO containing updated meeting details
     * @return updated {@link MeetingDto} with applied changes
     * @throws IllegalArgumentException if company or meeting not found
     */
    MeetingDto update(Integer companyId, Integer meetingId, MeetingUpdateDto meetingDto);
}
