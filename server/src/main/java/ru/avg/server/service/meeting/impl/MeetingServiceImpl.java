package ru.avg.server.service.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
import ru.avg.server.model.dto.meeting.mapper.MeetingMapper;
import ru.avg.server.model.dto.meeting.mapper.NewMeetingMapper;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.repository.meeting.MeetingRepository;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.utils.updater.Updater;
import ru.avg.server.utils.verifier.Verifier;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Implementation of {@link MeetingService} providing business logic for managing meeting entities.
 * This service handles the complete lifecycle of meetings within a company context, including
 * creation, retrieval, update, and deletion operations with proper validation and business rule enforcement.
 * <p>
 * The class is annotated with {@link Service} to indicate it's a Spring service component,
 * and uses {@link RequiredArgsConstructor} to generate a constructor for dependency injection
 * of all final fields. This promotes immutability and simplifies testing.
 * </p>
 *
 * @author AVG
 * @see MeetingService
 * @see MeetingRepository
 * @see MeetingMapper
 * @see NewMeetingMapper
 * @see Updater
 * @see Verifier
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    /**
     * Maximum number of meetings to return per page in paginated responses.
     * This value is injected from configuration using the property key "page.maxlimit.meeting"
     * and used to validate the limit parameter in paginated methods. It ensures consistent
     * pagination limits across the meeting service and prevents excessively large responses.
     * The value is validated by {@link Verifier#verifyPageAndLimit(Integer, Integer, Integer)}.
     *
     * @see #findAll(Integer, Integer, Integer)
     * @see Verifier#verifyPageAndLimit(Integer, Integer, Integer)
     */
    @Value("${page.maxlimit.meeting}")
    private Integer pageLimit;

    /**
     * Repository for managing persistence and retrieval of meeting entities.
     * This dependency provides CRUD operations and custom queries for meeting data.
     * Injected by Spring via constructor injection and used throughout the service
     * for all database interactions related to meetings.
     *
     * @see MeetingRepository
     */
    private final MeetingRepository meetingRepository;

    /**
     * Repository for managing meeting-participant relationships.
     * This dependency is used when clearing participants after a meeting type change,
     * as different meeting types may have different participant requirements and rules.
     * Injected by Spring via constructor injection.
     *
     * @see MeetingParticipantRepository
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Mapper responsible for bidirectional conversion between Meeting entities and MeetingDto objects.
     * This component handles the transformation of domain models to data transfer objects
     * for external communication and vice versa. It abstracts the mapping logic and ensures
     * consistent data representation across service boundaries.
     * Injected by Spring via constructor injection.
     *
     * @see MeetingMapper
     */
    private final MeetingMapper meetingMapper;

    /**
     * Mapper responsible for converting NewMeetingDto to Meeting entities during creation operations.
     * This specialized mapper handles the transformation of creation-specific DTOs to domain entities,
     * including resolving relationships and setting default values. It separates creation concerns
     * from general mapping responsibilities.
     * Injected by Spring via constructor injection.
     *
     * @see NewMeetingMapper
     */
    private final NewMeetingMapper newMeetingMapper;

    /**
     * Utility component used to perform partial updates on entities.
     * This service implements PATCH semantics by copying only non-null and non-blank fields
     * from a source object to a target object, preserving existing values for unspecified fields.
     * It abstracts the field-by-field comparison and copying logic, making update operations cleaner.
     * Injected by Spring via constructor injection.
     *
     * @see Updater
     */
    private final Updater updater;

    /**
     * Utility component responsible for verifying entity existence and relationships.
     * This service ensures that a company exists and that a meeting belongs to that company,
     * enforcing data isolation and access control across tenant boundaries. It centralizes
     * validation logic to prevent code duplication and ensure consistent security checks.
     * Injected by Spring via constructor injection.
     *
     * @see Verifier
     */
    private final Verifier verifier;

    /**
     * Creates and saves a new meeting for the specified company.
     * The method first verifies that the company exists using the {@link Verifier} component,
     * then converts the input DTO to an entity using {@link NewMeetingMapper}, sets the company
     * association, persists the entity via {@link MeetingRepository}, and returns the result
     * converted to a DTO using {@link MeetingMapper}.
     *
     * @param companyId     the unique identifier of the company that will own the meeting
     * @param newMeetingDto the data transfer object containing the details of the new meeting (must not be null)
     * @return a MeetingDto representing the saved meeting, including generated identifiers and system fields
     * @throws CompanyNotFound          if the specified company does not exist
     * @throws IllegalArgumentException if newMeetingDto is null
     * @see NewMeetingMapper#fromDto(NewMeetingDto)
     * @see MeetingMapper#toDto(Meeting)
     * @see MeetingRepository#save(Object)
     */
    @Override
    public MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        Meeting meeting = newMeetingMapper.fromDto(newMeetingDto);
        meeting.getCompany().setId(companyId); // Ensure company ID is set in entity
        return meetingMapper.toDto(meetingRepository.save(meeting));
    }

    /**
     * Updates an existing meeting with new values from the provided NewMeetingDto.
     * The method implements partial update semantics (PATCH) where only non-null and non-blank
     * fields from the source are applied to the target entity. Before updating, it verifies
     * that the meeting exists and belongs to the specified company.
     * <p>
     * Special business logic is applied when the meeting type changes: all associated participants
     * are cleared using {@link MeetingParticipantRepository#deleteByMeetingId(Integer)} because
     * different meeting types may have different participant requirements, quorum rules, or voting rights.
     * </p>
     *
     * @param companyId     the unique identifier of the company that owns the meeting
     * @param meetingId     the unique identifier of the meeting to update
     * @param newMeetingDto the data transfer object containing updated values (nullable fields are ignored)
     * @return a MeetingDto representing the updated meeting
     * @throws CompanyNotFound          if the specified company does not exist
     * @throws MeetingNotFound          if no meeting exists with the given meetingId
     * @throws IllegalArgumentException if newMeetingDto is null
     * @see MeetingMapper#toDto(Meeting)
     */
    @Override
    public MeetingDto update(Integer companyId, Integer meetingId, NewMeetingDto newMeetingDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        Meeting existingMeeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFound(meetingId));

        Meeting updateSource = newMeetingMapper.fromDto(newMeetingDto);
        updateSource.setId(existingMeeting.getId());

        // Only apply updates for non-null or non-blank fields
        if (Objects.nonNull(updateSource.getType()) && updateSource.getType() != existingMeeting.getType()) {
            // Clear participants when meeting type changes (different quorum/rules may apply)
            meetingParticipantRepository.deleteByMeetingId(meetingId);
        }

        existingMeeting = updater.update(existingMeeting, updateSource);

        Meeting updatedMeeting = meetingRepository.save(existingMeeting);
        return meetingMapper.toDto(updatedMeeting);
    }

    /**
     * Deletes a meeting identified by its ID, provided it belongs to the specified company.
     * The operation first verifies the existence of the company and the association between
     * the company and meeting using {@link Verifier#verifyCompanyAndMeeting(Integer, Integer)}.
     * It then checks if the meeting exists and throws {@link MeetingNotFound} if not found,
     * otherwise deletes the meeting via {@link MeetingRepository#deleteById(Object)}.
     * <p>
     * This two-step verification process ensures proper access control and provides
     * meaningful error messages to clients.
     * </p>
     *
     * @param companyId the unique identifier of the company that owns the meeting
     * @param meetingId the unique identifier of the meeting to delete
     * @throws MeetingNotFound if no meeting exists with the given meetingId
     * @throws CompanyNotFound if the specified company does not exist
     */
    @Override
    public void delete(Integer companyId, Integer meetingId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);
        if (!meetingRepository.existsById(meetingId)) {
            throw new MeetingNotFound(meetingId);
        }
        meetingRepository.deleteById(meetingId);
    }

    /**
     * Finds a meeting by its associated company, meeting type, and date combination.
     * This method serves as a specialized finder that uses business-relevant attributes
     * rather than the technical identifier. It first verifies the company existence,
     * then queries the repository for a meeting matching all three criteria.
     * <p>
     * The method is particularly useful in scenarios where clients know the logical
     * identity of a meeting (company, type, date) but not its technical ID, such as
     * when importing data or integrating with external systems.
     * </p>
     *
     * @param companyId the unique identifier of the company
     * @param type      the type/category of the meeting (e.g., BOARD, GENERAL, COMMITTEE)
     * @param date      the date on which the meeting was scheduled or held
     * @return a MeetingDto representing the found meeting
     * @throws MeetingNotFound if no meeting matches the given criteria
     * @throws CompanyNotFound if the specified company does not exist
     * @see MeetingRepository#findByCompanyIdAndTypeAndDate(Integer, MeetingType, LocalDate)
     */
    @Override
    public MeetingDto find(Integer companyId, MeetingType type, LocalDate date) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        return meetingRepository.findByCompanyIdAndTypeAndDate(companyId, type, date)
                .map(meetingMapper::toDto)
                .orElseThrow(() -> new MeetingNotFound(companyId, date));
    }

    /**
     * Retrieves a specific meeting by its unique identifier within the context of a company.
     * This method ensures data isolation by verifying that the requested meeting belongs
     * to the specified company before retrieval. It converts the found entity to a DTO
     * for external representation.
     * <p>
     * The company context parameter serves both as an access control mechanism and a
     * scoping device, supporting the multi-tenant architecture where each company's
     * data is isolated from others.
     * </p>
     *
     * @param companyId the unique identifier of the company that owns the meeting
     * @param meetingId the unique identifier of the meeting to retrieve
     * @return a MeetingDto representing the found meeting
     * @throws MeetingNotFound if no meeting exists with the given meetingId
     * @throws CompanyNotFound if the specified company does not exist
     * @see MeetingRepository#findById(Object)
     */
    @Override
    public MeetingDto findById(Integer companyId, Integer meetingId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);
        return meetingRepository.findById(meetingId)
                .map(meetingMapper::toDto)
                .orElseThrow(() -> new MeetingNotFound(meetingId));
    }

    /**
     * Retrieves a paginated list of meeting DTOs for a given company, sorted by meeting date in descending order.
     * <p>
     * This method first verifies that the specified company exists and is accessible by the current user or context.
     * It then validates the provided pagination parameters: {@code page} must be non-negative and {@code limit}
     * must be between 1 and 20 (inclusive). A {@link Pageable} object is created using these validated values
     * and passed to the repository to fetch the corresponding page of {@link Meeting} entities. The retrieved
     * entities are then converted to {@link MeetingDto} objects using the {@link MeetingMapper}.
     *
     * @param companyId the unique identifier of the company whose meetings are to be retrieved;
     *                  must correspond to an existing and accessible company, otherwise a {@link RuntimeException} may be thrown
     * @param page      the page number to retrieve (zero-based index); must not be {@code null} and must be greater than
     *                  or equal to zero, otherwise an {@link IllegalArgumentException} is thrown
     * @param limit     the maximum number of meetings to return per page; must not be {@code null} and must be
     *                  between 1 and 20 (inclusive), otherwise an {@link IllegalArgumentException} is thrown
     * @return a {@link Page} containing the requested slice of {@link MeetingDto} objects,
     * sorted by meeting date in descending order (newest first); never {@code null}
     * @throws IllegalArgumentException if {@code page} is negative or {@code limit} is not in the range [1, 20]
     * @throws RuntimeException         if the company verification fails (e.g., company does not exist or access is denied)
     * @see MeetingRepository#findByCompanyIdOrderByDateDesc(Integer, Pageable)
     * @see MeetingMapper#toDto(Meeting)
     * @see Page
     * @see PageRequest
     */
    @Override
    public Page<MeetingDto> findAll(Integer companyId, Integer page, Integer limit) {
        verifier.verifyCompany(companyId);

        verifier.verifyPageAndLimit(page, limit, pageLimit);

        Pageable pageable = PageRequest.of(page, limit);

        return meetingRepository.findByCompanyIdOrderByDateDesc(companyId, pageable)
                .map(meetingMapper::toDto);
    }
}