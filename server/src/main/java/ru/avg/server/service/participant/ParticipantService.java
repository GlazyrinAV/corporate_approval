package ru.avg.server.service.participant;

import ru.avg.server.model.dto.participant.NewParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.ParticipantType;

import java.util.List;

/**
 * Service interface defining the contract for managing participant entities within the approval system.
 * This interface provides methods for creating, updating, deleting, and retrieving participants,
 * with all operations scoped to a specific company context to enforce access control and data isolation.
 * <p>
 * The service supports full CRUD operations along with specialized retrieval methods based on
 * business requirements. All methods require a companyId parameter to ensure proper scoping,
 * supporting a multi-tenant architecture where each company's data is isolated from others.
 * </p>
 *
 * @author AVG
 * @since 1.0
 */
public interface ParticipantService {

    /**
     * Creates a new participant for a specific company.
     * This operation persists a new participant entity with the provided data, assigning a unique
     * identifier and setting system-managed fields (like creation timestamp).
     * <p>
     * The participant is associated with the specified company, establishing ownership and access control.
     * Validation is performed to ensure the company exists and the participant data is valid before persistence.
     * </p>
     *
     * @param companyId         the ID of the company that will own the participant; must not be null
     * @param newParticipantDto the data transfer object containing all required participant details; must not be null
     * @return the saved ParticipantDto containing the generated ID and any system-assigned fields
     * @throws IllegalArgumentException                        if companyId is null or newParticipantDto is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    ParticipantDto save(Integer companyId, NewParticipantDto newParticipantDto);

    /**
     * Updates an existing participant with new information.
     * This method implements partial update semantics (PATCH) where only non-null fields
     * from the provided DTO are applied to the existing entity, leaving other fields unchanged.
     * <p>
     * The operation requires both the companyId and participantId to locate the specific participant
     * within the proper company context, preventing unauthorized cross-company modifications.
     * Special validation is performed when changing participant type to prevent conflicts.
     * </p>
     *
     * @param companyId         the ID of the company that owns the participant; must not be null
     * @param participantId     the ID of the participant to update; must not be null
     * @param newParticipantDto the DTO containing the fields to update; nullable fields are ignored during update
     * @return the updated ParticipantDto reflecting the changes in the database
     * @throws ru.avg.server.exception.company.CompanyNotFound             if the specified company does not exist
     * @throws ru.avg.server.exception.participant.ParticipantNotFound     if the specified participant does not exist
     * @throws ru.avg.server.exception.participant.ParticipantAlreadyExist if updating would create a duplicate participant
     */
    ParticipantDto update(Integer companyId, Integer participantId, NewParticipantDto newParticipantDto);

    /**
     * Deletes a participant identified by its ID from a specific company.
     * This operation removes the participant from the system with special behavior based on usage:
     * <ul>
     *   <li>If the participant is not assigned to any active meetings: physically deletes the record</li>
     *   <li>If the participant is assigned to active meetings: deactivates the participant (isActive = false) instead</li>
     * </ul>
     * <p>
     * The company context is required to ensure the client has proper authorization to delete
     * the specified participant, maintaining data security and integrity.
     * </p>
     *
     * @param companyId     the ID of the company that owns the participant
     * @param participantId the ID of the participant to delete
     * @throws ru.avg.server.exception.company.CompanyNotFound         if the specified company does not exist
     * @throws ru.avg.server.exception.participant.ParticipantNotFound if the specified participant does not exist
     */
    void delete(Integer companyId, Integer participantId);

    /**
     * Finds a participant by name, company, and participant type combination.
     * This specialized search method locates a participant using business-relevant criteria:
     * the owning company, the participant's name, and their participant type (e.g., owner, board member).
     * <p>
     * This method is particularly useful for scenarios where business logic needs to verify
     * the existence of a specific participant configuration before allowing certain operations.
     * </p>
     *
     * @param name      the name/title of the participant to search for (e.g., "John Doe", "ABC Corporation")
     * @param companyId the ID of the company where the search is performed
     * @param type      the type/category of the participant (e.g., OWNER, MEMBER_OF_BOARD)
     * @return the ParticipantDto representing the matching participant
     * @throws ru.avg.server.exception.company.CompanyNotFound         if the specified company does not exist
     * @throws ru.avg.server.exception.participant.ParticipantNotFound if no participant matches the criteria
     */
    ParticipantDto find(String name, Integer companyId, ParticipantType type);

    /**
     * Retrieves all participants eligible for a specific meeting type within a company.
     * This method filters participants based on business rules related to meeting types:
     * <ul>
     *   <li>For BOARD meetings: only returns participants of type MEMBER_OF_BOARD</li>
     *   <li>For FMS/FMP meetings: only returns participants of type OWNER</li>
     * </ul>
     * <p>
     * This functionality supports proper participant selection for different meeting types,
     * ensuring that only eligible participants are considered for specific meeting categories.
     * </p>
     *
     * @param companyId the ID of the company for which to retrieve eligible participants
     * @param type      the type of meeting used to filter participants by eligibility rules
     * @return a list of ParticipantDto objects matching the eligibility criteria; never null but may be empty
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    List<ParticipantDto> findAllByMeetingType(Integer companyId, MeetingType type);

    /**
     * Retrieves all participants associated with a specific company.
     * This method returns a complete list of participants for the given company identifier.
     * <p>
     * The result is never null but may be an empty list if no participants exist for the company.
     * Participants are typically returned in their natural order (by ID), though the exact
     * ordering may depend on the implementation.
     * </p>
     *
     * @param companyId the ID of the company for which to retrieve all participants
     * @return a list of ParticipantDto objects representing all participants for the company; never null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     */
    List<ParticipantDto> findAll(Integer companyId);

    /**
     * Retrieves a specific participant by its unique identifier within a company context.
     * This method fetches complete participant information including all associated data.
     * <p>
     * The operation requires both identifiers to ensure proper scoping and access control,
     * preventing unauthorized access to participants across different companies.
     * </p>
     *
     * @param companyId     the ID of the company that owns the participant
     * @param participantId the unique identifier of the participant to retrieve
     * @return the ParticipantDto containing all information about the requested participant
     * @throws ru.avg.server.exception.company.CompanyNotFound         if the specified company does not exist
     * @throws ru.avg.server.exception.participant.ParticipantNotFound if the specified participant does not exist
     */
    ParticipantDto findById(Integer companyId, Integer participantId);
}