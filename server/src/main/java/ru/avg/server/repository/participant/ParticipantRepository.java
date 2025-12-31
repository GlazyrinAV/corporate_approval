package ru.avg.server.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Participant} entities.
 * Provides CRUD operations and custom query methods for participant data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient retrieval of participants
 * by name, company, type, and other attributes.
 * </p>
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    /**
     * Finds a participant by name, company ID, and participant type.
     * Used to ensure uniqueness of participants within a company context.
     *
     * @param name the name of the participant; must not be null
     * @param companyId the ID of the associated company; must not be null
     * @param type the type of the participant (e.g., Owner, Board Member); must not be null
     * @return an {@link Optional} containing the found participant, or empty if not found
     */
    Optional<Participant> findByNameAndCompanyIdAndType(
            @Param("name") String name,
            @Param("companyId") Integer companyId,
            @Param("type") ParticipantType type);

    /**
     * Retrieves all participants associated with a specific company.
     *
     * @param companyId the ID of the company; must not be null
     * @return a list of participants, never null
     */
    List<Participant> findAllByCompanyId(@Param("companyId") Integer companyId);

    /**
     * Counts the number of active participants in a given company.
     *
     * @param companyId the ID of the company
     * @return the count of active participants
     */
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.company.id = :companyId AND p.isActive = true")
    long countActiveByCompanyId(@Param("companyId") Integer companyId);
}