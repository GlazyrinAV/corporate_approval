package ru.avg.server.service.participant;

import org.springframework.data.domain.Page;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;

import java.util.List;

/**
 * Service interface for managing participants in a meeting.
 * Defines operations to add, retrieve, and remove participants within the context
 * of a specific company and meeting, ensuring data isolation and access control.
 * <p>
 * This service provides methods for:
 * <ul>
 *   <li>Adding single or multiple participants to meetings</li>
 *   <li>Retrieving all participants associated with a meeting</li>
 *   <li>Finding potential participants for current or new meetings</li>
 *   <li>Removing participants from meetings</li>
 * </ul>
 * All operations are scoped to a specific company and meeting to enforce access control.
 * </p>
 *
 * @author AVG
 * @see MeetingParticipantDto
 * @see MeetingParticipant
 * @see ru.avg.server.service.participant.impl.MeetingParticipantServiceImpl
 * @since 1.0
 */
public interface MeetingParticipantService {

    /**
     * Adds multiple participants to the specified meeting.
     *
     * @param companyId    the ID of the company that owns the meeting
     * @param meetingId    the ID of the meeting to which participants will be added
     * @param participants the list of MeetingParticipantDto objects containing participant details
     * @return a list of saved MeetingParticipantDto objects, including any generated fields such as IDs or default roles
     */
    List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants);

    /**
     * Adds a single participant to a meeting with validation that the participant belongs to the specified company.
     * <p>
     * This method first verifies that the company and meeting exist and are properly associated
     * via {@link ru.avg.server.utils.verifier.Verifier#verifyCompanyAndMeeting(Integer, Integer)}.
     * It then checks that the participant belongs to the specified company using
     * {@link ru.avg.server.utils.verifier.Verifier#verifyParticipant(Integer, Integer, Integer)}.
     * After validation, a new meeting participant record is created and returned.
     * </p>
     *
     * @param companyId             the ID of the company that owns the meeting; must not be {@code null} and must exist
     * @param meetingId             the ID of the meeting to which the participant will be added; must not be {@code null} and must exist
     * @param meetingParticipantDto the DTO of the participant to add; must not be {@code null}
     * @return the saved {@link MeetingParticipantDto} with generated fields such as ID
     * @throws ru.avg.server.exception.company.CompanyNotFound                if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound                if the specified meeting does not exist
     * @throws ru.avg.server.exception.participant.MeetingParticipantNotFound if the participant does not belong to the specified company
     * @see ru.avg.server.utils.verifier.Verifier
     */
    MeetingParticipantDto save(Integer companyId, Integer meetingId, MeetingParticipantDto meetingParticipantDto);


    /**
     * Retrieves all participants associated with a given meeting.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting
     * @return a list of MeetingParticipantDto objects representing all participants in the meeting;
     * returns an empty list if no participants are found
     */
    Page<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId, Integer page, Integer limit);

    /**
     * Retrieves potential participants who can be added to the meeting.
     * This may include employees or members of the company who are not yet part of the meeting.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting for which potential participants are being retrieved
     * @return a list of MeetingParticipantDto objects representing potential participants;
     * returns an empty list if no potential participants are available
     */
    Page<MeetingParticipantDto> findPotentialInCurrentMeeting(Integer companyId, Integer meetingId, Integer page, Integer limit);

    /**
     * Finds potential participants for creating a new meeting of the specified type.
     * <p>
     * Unlike {@link #findPotentialInCurrentMeeting(Integer, Integer, Integer, Integer)}, this method does not
     * filter participants by an existing meeting. Instead, it returns all active participants
     * matching the specified meeting type.
     * </p>
     *
     * @param companyId    the ID of the company; used for validation and participant lookup
     * @param meetingType  the name of the meeting type (from {@link ru.avg.server.model.meeting.MeetingType#getTitle()});
     *                     used to filter participants by meeting type
     * @param page         the zero-based page number
     * @param limit        the maximum number of elements per page
     * @return a {@link Page} of {@link MeetingParticipantDto} representing available participants for the specified meeting type;
     * returns an empty page if no potential participants are found
     * @throws ru.avg.server.exception.company.CompanyNotFound     if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingTypeNotFound if the meeting type is invalid or not supported
     * @see ru.avg.server.model.meeting.MeetingType
     */
    Page<MeetingParticipantDto> findPotentialInNewMeeting(Integer companyId, String meetingType, Integer page, Integer limit);

    /**
     * Retrieves a specific participant by their participant ID within the context of a meeting and company.
     *
     * @param companyId     the ID of the company
     * @param meetingId     the ID of the meeting
     * @param participantId the ID of the participant to retrieve
     * @return the MeetingParticipantDto corresponding to the given participant ID
     */
    MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId);

    /**
     * Retrieves all meetings in which a specific participant is involved.
     * <p>
     * This method returns all meeting participant records for the specified participant,
     * with company validation to ensure access control.
     * </p>
     *
     * @param companyId     the ID of the company; used for validation
     * @param participantId the ID of the participant to search for
     * @return a list of {@link MeetingParticipantDto} representing all meetings the participant is involved in;
     * returns an empty list if the participant is not found
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @see ru.avg.server.utils.verifier.Verifier
     */
    List<MeetingParticipantDto> findAllByParticipantId(Integer companyId, Integer participantId);


    /**
     * Removes a participant from a meeting.
     *
     * @param companyId            the ID of the company that owns the meeting
     * @param meetingId            the ID of the meeting from which the participant will be removed
     * @param meetingParticipantId the ID of the meeting participant entity to delete
     */
    void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId);

    /**
     * Removes all participants associated with a specific meeting.
     * <p>
     * This method performs a cascade deletion of all {@link MeetingParticipant} records
     * associated with the specified meeting. It is typically used when deleting a meeting
     * or clearing its participant roster.
     * </p>
     *
     * @param meetingId the ID of the meeting for which all participants should be removed
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     * @see MeetingParticipant
     */
    void deleteByMeetingId(Integer meetingId);
}