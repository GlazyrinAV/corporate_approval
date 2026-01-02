package ru.avg.server.service.participant;

import ru.avg.server.model.dto.participant.MeetingParticipantDto;

import java.util.List;

/**
 * Service interface for managing participants in a meeting.
 * Defines operations to add, retrieve, and remove participants within the context
 * of a specific company and meeting, ensuring data isolation and access control.
 */
public interface MeetingParticipantService {

    /**
     * Adds multiple participants to the specified meeting.
     *
     * @param companyId the ID of the company that owns the meeting
     * @param meetingId the ID of the meeting to which participants will be added
     * @param participants the list of MeetingParticipantDto objects containing participant details
     * @return a list of saved MeetingParticipantDto objects, including any generated fields such as IDs or default roles
     */
    List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants);

    /**
     * Retrieves all participants associated with a given meeting.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting
     * @return a list of MeetingParticipantDto objects representing all participants in the meeting;
     *         returns an empty list if no participants are found
     */
    List<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId);

    /**
     * Retrieves potential participants who can be added to the meeting.
     * This may include employees or members of the company who are not yet part of the meeting.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting for which potential participants are being retrieved
     * @return a list of MeetingParticipantDto objects representing potential participants;
     *         returns an empty list if no potential participants are available
     */
    List<MeetingParticipantDto> findPotential(Integer companyId, Integer meetingId);

    /**
     * Retrieves a specific participant by their participant ID within the context of a meeting and company.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting
     * @param participantId the ID of the participant to retrieve
     * @return the MeetingParticipantDto corresponding to the given participant ID
     */
    MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId);

    /**
     * Removes a participant from a meeting.
     *
     * @param companyId the ID of the company that owns the meeting
     * @param meetingId the ID of the meeting from which the participant will be removed
     * @param meetingParticipantId the ID of the meeting participant entity to delete
     */
    void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId);
}