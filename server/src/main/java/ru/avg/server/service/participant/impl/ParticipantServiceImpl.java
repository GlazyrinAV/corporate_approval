package ru.avg.server.service.participant.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.participant.ParticipantAlreadyExist;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.participant.NewParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.dto.participant.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.participant.mapper.NewParticipantMapper;
import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.participant.ParticipantRepository;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.utils.updater.Updater;
import ru.avg.server.utils.verifier.Verifier;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link ParticipantService} providing business logic for managing participant entities.
 * This service handles creation, retrieval, update, and deletion of participants with validation,
 * type conflict checking, and lifecycle management based on meeting assignments.
 *
 * <p>The class is annotated with {@link Service} to indicate it's a Spring service component,
 * and uses {@link RequiredArgsConstructor} to generate a constructor for dependency injection.</p>
 *
 * <p>This implementation supports partial updates (PATCH semantics), where only non-null or non-blank
 * fields from the update DTO are applied to the existing entity. The actual field-level merging
 * is delegated to the {@link Updater} utility class.</p>
 *
 * @author AVG
 * @see ParticipantService
 * @see ParticipantRepository
 * @see ParticipantMapper
 * @see NewParticipantMapper
 * @see Updater
 * @see Verifier
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    /**
     * Repository for managing participant entities in the persistence layer.
     * Provides CRUD operations and custom queries for participant data.
     * Injected by Spring and used throughout the service for database interactions.
     */
    private final ParticipantRepository participantRepository;

    /**
     * Repository for managing meeting-participant relationships.
     * Used to check if a participant is assigned to any active meetings before deletion.
     * Injected by Spring and used to enforce business rules around participant lifecycle.
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Mapper for converting between Participant entities and ParticipantDto objects.
     * Handles bidirectional transformation for full participant representations.
     * Injected by Spring and used when returning participant data to clients.
     */
    private final ParticipantMapper participantMapper;

    /**
     * Mapper for converting between MeetingParticipant entities and MeetingParticipantDto objects.
     * Used when checking the active status of a participant's meeting assignments.
     * Injected by Spring and supports the delete operation logic.
     */
    private final MeetingParticipantMapper meetingParticipantMapper;

    /**
     * Mapper for converting NewParticipantDto objects to Participant entities.
     * Used when creating new participants from API requests.
     * Contains logic for resolving participant types from string values.
     * Injected by Spring and ensures proper entity construction from DTOs.
     */
    private final NewParticipantMapper newParticipantMapper;

    /**
     * Utility service for verifying the existence of related entities.
     * Used to validate that the specified company exists before processing operations.
     * Provides centralized validation logic to maintain data integrity.
     * Injected by Spring and called at the beginning of most service methods.
     */
    private final Verifier verifier;

    /**
     * Utility service for performing partial updates on entities.
     * Implements PATCH semantics by copying only non-null/meaningful values
     * from the source to target entity, preserving existing values.
     * Injected by Spring and used in the update operation.
     */
    private final Updater updater;

    /**
     * Saves a new participant for the specified company.
     * <p>Validates company existence before saving. The input DTO is converted to an entity
     * using {@link NewParticipantMapper}, persisted via the repository, and returned as a DTO.</p>
     *
     * @param companyId         the ID of the company to which the participant belongs
     * @param newParticipantDto the DTO containing participant data (must not be null)
     * @return the saved ParticipantDto with generated ID and system-assigned fields
     * @throws IllegalArgumentException                        if newParticipantDto is null
     * @throws ru.avg.server.exception.company.CompanyNotFound if the specified company does not exist
     * @see NewParticipantMapper#fromDto(NewParticipantDto)
     * @see ParticipantMapper#toDto(Participant)
     */
    @Override
    public ParticipantDto save(Integer companyId, NewParticipantDto newParticipantDto) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        return participantMapper.toDto(
                participantRepository.save(newParticipantMapper.fromDto(newParticipantDto))
        );
    }

    /**
     * Updates an existing participant with non-null or non-blank fields from the provided DTO.
     * Only the fields that are present in the DTO are updated (partial update semantics).
     * <p>The actual field comparison and copying is handled by the {@link Updater} class,
     * which ensures that only meaningful values (non-null, non-blank strings) are applied
     * from the update source to the existing entity.</p>
     *
     * <p>Special handling is performed when changing participant type:
     * <ul>
     *   <li>If the new type would create a conflict with another participant (same name, share, inn, and new type),
     *       a {@link ParticipantAlreadyExist} exception is thrown.</li>
     *   <li>Otherwise, the type is updated successfully.</li>
     * </ul>
     * </p>
     *
     * @param companyId         the ID of the company to which the participant belongs
     * @param participantId     the ID of the participant to update
     * @param newParticipantDto the DTO containing updated values (nullable fields are ignored)
     * @return the updated ParticipantDto reflecting changes in the database
     * @throws ParticipantNotFound      if no participant exists with the given ID
     * @throws ParticipantAlreadyExist  if updating the type would create a duplicate
     * @throws IllegalArgumentException if updatedParticipantDto is null
     * @see ParticipantMapper#toDto(Participant)
     */
    @Override
    @Transactional
    public ParticipantDto update(Integer companyId, Integer participantId, NewParticipantDto newParticipantDto) {
        verifier.verifyCompanyAndMeeting(companyId, null);

        Participant existingParticipant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFound(participantId));

        Participant updateSource = newParticipantMapper.fromDto(newParticipantDto);
        Participant updatedParticipant = updater.update(existingParticipant, updateSource);

        // Handle type change with conflict detection
        if (!Objects.equals(updateSource.getType(), existingParticipant.getType())) {
            boolean conflictExists = participantRepository.findAllByCompanyId(companyId).stream()
                    .anyMatch(p -> !Objects.equals(p.getId(), participantId) && // Exclude current participant
                            p.getName().equals(existingParticipant.getName()) &&
                            p.getShare().equals(existingParticipant.getShare()) &&
                            p.getCompany().getInn().equals(existingParticipant.getCompany().getInn()) &&
                            p.getType().equals(updateSource.getType()));

            if (conflictExists) {
                throw new ParticipantAlreadyExist(existingParticipant.getName());
            }
            updatedParticipant.setType(updateSource.getType());
        }

        return participantMapper.toDto(participantRepository.save(updatedParticipant));
    }

    /**
     * Deletes a participant by its ID.
     * <p>Behavior depends on whether the participant is assigned to active meetings:
     * <ul>
     *   <li>If no active meetings: physically deletes the participant record.</li>
     *   <li>If assigned to meetings: deactivates the participant ({@code isActive = false}) instead of deletion.</li>
     * </ul>
     * </p>
     *
     * @param companyId     the ID of the company to which the participant belongs
     * @param participantId the ID of the participant to delete
     * @throws ParticipantNotFound if no participant exists with the given ID
     * @see MeetingParticipantRepository#findByParticipantId(Integer)
     */
    @Override
    public void delete(Integer companyId, Integer participantId) {
        verifier.verifyCompanyAndMeeting(companyId, null);

        boolean hasActiveMeetings = meetingParticipantRepository.findByParticipantId(participantId).stream()
                .map(meetingParticipantMapper::toDto)
                .anyMatch(meetingParticipantDto ->
                        Boolean.TRUE.equals(meetingParticipantDto.getParticipant().getIsActive()));

        if (!hasActiveMeetings) {
            participantRepository.deleteById(participantId);
        } else {
            Participant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new ParticipantNotFound(participantId));
            participant.setIsActive(false);
            participantRepository.save(participant);
        }
    }

    /**
     * Retrieves a participant by name, company, and type.
     *
     * @param name      the name of the participant to find
     * @param companyId the ID of the company
     * @param type      the type of the participant
     * @return the matching ParticipantDto
     * @throws ParticipantNotFound if no matching participant is found
     */
    @Override
    public ParticipantDto find(String name, Integer companyId, ParticipantType type) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        return participantMapper.toDto(
                participantRepository.findByNameAndCompanyIdAndType(name, companyId, type)
                        .orElseThrow(() -> new ParticipantNotFound(name))
        );
    }

    /**
     * Retrieves participants filtered by meeting type eligibility.
     * <p>Filtering rules:
     * <ul>
     *   <li>{@link MeetingType#BOD}: Only include {@link ParticipantType#MEMBER_OF_BOARD}</li>
     *   <li>{@link MeetingType#FMS} or {@link MeetingType#FMP}: Only include {@link ParticipantType#OWNER}</li>
     * </ul>
     * </p>
     *
     * @param companyId the ID of the company
     * @param type      the meeting type to filter by
     * @return list of eligible ParticipantDto objects
     */
    @Override
    public List<ParticipantDto> findAllByMeetingType(Integer companyId, MeetingType type) {
        verifier.verifyCompanyAndMeeting(companyId, null);

        return participantRepository.findAllByCompanyId(companyId).stream()
                .filter(participant -> {
                    if (type == MeetingType.BOD) {
                        return participant.getType() == ParticipantType.MEMBER_OF_BOARD;
                    } else {
                        return participant.getType() == ParticipantType.OWNER;
                    }
                })
                .map(participantMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all participants for a company.
     *
     * @param companyId the ID of the company
     * @return list of all ParticipantDto objects for the company
     */
    @Override
    public List<ParticipantDto> findAll(Integer companyId) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        return participantRepository.findAllByCompanyId(companyId).stream()
                .map(participantMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a participant by its ID.
     *
     * @param companyId     the ID of the company to which the participant belongs
     * @param participantId the ID of the participant to retrieve
     * @return the corresponding ParticipantDto
     * @throws ParticipantNotFound if no participant exists with the given ID
     */
    @Override
    public ParticipantDto findById(Integer companyId, Integer participantId) {
        verifier.verifyCompanyAndMeeting(companyId, null);
        return participantMapper.toDto(
                participantRepository.findById(participantId)
                        .orElseThrow(() -> new ParticipantNotFound(participantId))
        );
    }
}