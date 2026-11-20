package ru.avg.server.mservice.participant.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.mservice.participant.ParticipantMService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.utils.verifier.Verifier;

/**
 * Implementation of {@link ParticipantMService} for managing participants at the microservice layer.
 * Handles participant deletion with soft-delete support based on active meeting participation.
 * <p>
 * This service determines whether a participant should be permanently deleted or soft-deleted
 * (deactivated) by checking their involvement in active meetings. Participants with active
 * meetings are soft-deleted by setting their {@code isActive} flag to false.
 * </p>
 *
 * @author AVG
 * @see ParticipantMService
 * @see ru.avg.server.model.participant.Participant
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class ParticipantMServiceImpl implements ParticipantMService {

    /**
     * Utility component for verifying company existence.
     */
    private final Verifier verifier;

    /**
     * Service for managing meeting-participant associations; used to check if a participant has active meetings.
     */
    private final MeetingParticipantService meetingParticipantService;

    /**
     * Service for managing individual participants; used to fetch and delete participants.
     */
    private final ParticipantService participantService;

    /**
     * Mapper for converting {@link ru.avg.server.model.participant.Participant} entities to {@link ru.avg.server.model.dto.participant.ParticipantDto} DTOs.
     */
    private final ParticipantMapper participantMapper;

    /**
     * Deletes or deactivates a participant based on their active meeting participation.
     * <p>
     * This method first verifies the company existence via {@link Verifier#verifyCompany(Integer)}.
     * Then it checks if the participant has any active meetings via
     * {@link ru.avg.server.service.participant.MeetingParticipantService#findAllByParticipantId(Integer, Integer)}.
     * If no active meetings exist, the participant is permanently deleted. Otherwise, their active status
     * is set to false (soft delete).
     * </p>
     *
     * @param companyId     ID of the company; must exist
     * @param participantId ID of the participant to delete or deactivate
     * @throws IllegalArgumentException if the company does not exist
     */
    @Override
    public void delete(Integer companyId, Integer participantId) {
        verifier.verifyCompany(companyId);

        boolean hasActiveMeetings = meetingParticipantService.findAllByParticipantId(companyId, participantId)
                .stream()
                .anyMatch(meetingParticipantDto -> {
                            ParticipantDto participant = participantService.findById(companyId, participantId);

                            return Boolean.TRUE.equals(participant.getIsActive());
                        }
                );
        if (!hasActiveMeetings) {
            participantService.delete(companyId, participantId);
        } else {
            Participant participant = participantMapper.fromDto(participantService.findById(companyId, participantId));
            participant.setIsActive(false);
            participantService.update(companyId, participantId, participantMapper.toDto(participant));
        }
    }
}
