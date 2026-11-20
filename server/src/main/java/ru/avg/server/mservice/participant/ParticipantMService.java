package ru.avg.server.mservice.participant;

/**
 * Service interface for managing participants at the microservice layer.
 * Provides operations for participant deletion with soft-delete support.
 * <p>
 * This interface handles the logic for determining whether a participant should be
 * permanently deleted or soft-deleted (deactivated) based on their active meeting participation.
 * </p>
 *
 * @author AVG
 * @see ru.avg.server.model.participant.Participant
 * @see ru.avg.server.model.dto.participant.ParticipantDto
 * @since 1.0
 */
public interface ParticipantMService {

    /**
     * Deletes or deactivates a participant based on their active meeting participation.
     * <p>
     * This method first verifies the company existence via {@link Verifier#verifyCompany(Integer)}.
     * If the participant has no active meetings, they are permanently deleted via
     * {@link ru.avg.server.service.participant.ParticipantService#delete(Integer, Integer)}. Otherwise,
     * their active status is set to false (soft delete).
     * </p>
     *
     * @param companyId     ID of the company; must exist
     * @param participantId ID of the participant to delete or deactivate
     * @throws IllegalArgumentException if the company does not exist
     */
    void delete(Integer companyId, Integer participantId);

}
