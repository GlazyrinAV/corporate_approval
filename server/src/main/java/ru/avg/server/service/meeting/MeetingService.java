package ru.avg.server.service.meeting;

import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
import ru.avg.server.model.meeting.MeetingType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining the contract for managing meeting entities within the approval system.
 * This interface provides methods for creating, updating, deleting, and retrieving meetings,
 * with all operations scoped to a specific company context to enforce access control and data isolation.
 * <p>
 * The service supports full CRUD operations along with specialized search capabilities based on
 * business requirements. All methods require a companyId parameter to ensure proper scoping,
 * supporting a multi-tenant architecture where each company's data is isolated from others.
 * </p>
 *
 * @author AVG
 * @since 1.0
 */
public interface MeetingService {

    /**
     * Creates a new meeting for a specific company.
     * This operation persists a new meeting entity with the provided data, assigning a unique
     * identifier and setting system-managed fields (like creation timestamp).
     * <p>
     * The meeting is associated with the specified company, establishing ownership and access control.
     * Validation is performed to ensure the company exists and the meeting data is valid before persistence.
     * </p>
     *
     * @param companyId     the ID of the company that will own the meeting; must not be null
     * @param newMeetingDto the data transfer object containing all required meeting details; must not be null
     * @return the saved MeetingDto containing the generated ID and any system-assigned fields
     * @throws IllegalArgumentException                        if companyId is null or newMeetingDto is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto);

    /**
     * Updates an existing meeting with new information.
     * This method implements partial update semantics (PATCH) where only non-null fields
     * from the provided DTO are applied to the existing entity, leaving other fields unchanged.
     * <p>
     * The operation requires both the companyId and meetingId to locate the specific meeting
     * within the proper company context, preventing unauthorized cross-company modifications.
     * </p>
     *
     * @param companyId     the ID of the company that owns the meeting; must not be null
     * @param meetingId     the ID of the meeting to update; must not be null
     * @param newMeetingDto the DTO containing the fields to update; nullable fields are ignored during update
     * @return the updated MeetingDto reflecting the changes in the database
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     */
    MeetingDto update(Integer companyId, Integer meetingId, NewMeetingDto newMeetingDto);

    /**
     * Deletes a meeting identified by its ID from a specific company.
     * This operation permanently removes the meeting and all its associated data (topics, votes, etc.)
     * from the system, cascading through related entities.
     * <p>
     * The company context is required to ensure the client has proper authorization to delete
     * the specified meeting, maintaining data security and integrity.
     * </p>
     *
     * @param companyId the ID of the company that owns the meeting
     * @param meetingId the ID of the meeting to delete
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     */
    void delete(Integer companyId, Integer meetingId);

    /**
     * Finds a meeting by company, type, and date combination.
     * This specialized search method locates a meeting using business-relevant criteria:
     * the owning company, the meeting type (e.g., board meeting, general meeting), and the date.
     * <p>
     * This method is particularly useful for scenarios where a unique meeting needs to be
     * retrieved based on its scheduling information rather than its technical identifier.
     * </p>
     *
     * @param companyId the ID of the company that owns the meeting
     * @param type      the type/category of the meeting (e.g., BOARD, GENERAL, COMMITTEE)
     * @param date      the date when the meeting was scheduled or held
     * @return the MeetingDto representing the matching meeting
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if no meeting matches the criteria
     */
    MeetingDto find(Integer companyId, MeetingType type, LocalDate date);

    /**
     * Retrieves a specific meeting by its unique identifier within a company context.
     * This method fetches complete meeting information including all associated data.
     * <p>
     * The operation requires both identifiers to ensure proper scoping and access control,
     * preventing unauthorized access to meetings across different companies.
     * </p>
     *
     * @param companyId the ID of the company that owns the meeting
     * @param meetingId the unique identifier of the meeting to retrieve
     * @return the MeetingDto containing all information about the requested meeting
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @throws ru.avg.server.exception.meeting.MeetingNotFound if the specified meeting does not exist
     */
    MeetingDto findById(Integer companyId, Integer meetingId);

    /**
     * Retrieves all meetings associated with a specific company.
     * This method returns a complete list of meetings for the given company identifier.
     * <p>
     * The result is never null but may be an empty list if no meetings exist for the company.
     * Meetings are typically returned in chronological order by date, though the exact
     * ordering may depend on the implementation.
     * </p>
     *
     * @param companyId the ID of the company for which to retrieve all meetings
     * @return a list of MeetingDto objects representing all meetings for the company; never null
     */
    List<MeetingDto> findAll(Integer companyId);
}