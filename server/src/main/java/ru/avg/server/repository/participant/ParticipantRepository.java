package ru.avg.server.repository.participant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param name      the name of the participant; must not be null
     * @param companyId the ID of the associated company; must not be null
     * @param type      the type of the participant (e.g., Owner, Board Member); must not be null
     * @return an {@link Optional} containing the found participant, or empty if not found
     */
    Optional<Participant> findByNameAndCompanyIdAndType(
            @Param("name") String name,
            @Param("companyId") Integer companyId,
            @Param("type") ParticipantType type);

    /**
     * Retrieves a paginated list of all participants associated with a specific company,
     * sorted by participant name in ascending order.
     *
     * @param companyId the ID of the company; must not be null
     * @param pageable  the pagination information including page number and page size; must not be null
     * @return a {@link Page} of {@link Participant} entities containing the participants for the requested page,
     * including full pagination metadata (total elements, total pages, etc.), never null
     */
    @Query("SELECT Participant AS P FROM Participant WHERE P.company.id = :companyId ORDER BY P.name")
    Page<Participant> findAllByCompanyId(Integer companyId, Pageable pageable);

    /**
     * Retrieves all participants associated with a specific company, sorted by participant name in ascending order.
     *
     * @param companyId the ID of the company; must not be null
     * @return a list of participants, never null
     */
    List<Participant> findAllByCompanyId(Integer companyId);

    /**
     * Counts the number of active participants in a given company.
     *
     * @param companyId the ID of the company; must not be null
     * @return the count of active participants
     */
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.company.id = :companyId AND p.isActive = true")
    long countActiveByCompanyId(@Param("companyId") Integer companyId);

    /**
     * Finds participants matching the specified criteria within a given company with pagination support.
     * <p>
     * This method performs a case-insensitive partial match search on participant names that belong to the
     * specified company. The search is implemented using a JPQL query with pagination applied at the
     * database level for efficient handling of large datasets.
     * </p>
     * <p>
     * The search is scoped to a specific company using the {@code companyId} parameter, ensuring that
     * results are tenant-isolated. The criteria is matched against the participant's name using
     * case-insensitive LIKE pattern matching with wildcards on both sides (contains search).
     * </p>
     * <p>
     * Results are sorted by participant name in ascending order.
     * </p>
     *
     * @param companyId the unique identifier of the company to which participants must belong;
     *                  used to scope the search to a specific tenant
     * @param criteria  the search string to match against participant names; used in a case-insensitive
     *                  partial match (contains) operation; should be non-null for meaningful results
     * @param page      the pagination information including page number and size; must not be {@code null}
     * @return a {@link Page} of {@link Participant} entities containing the matching results for the
     * requested page, including full pagination metadata (total elements, total pages, etc.)
     * @see Page
     * @see Pageable
     * @see Participant
     */
    @Query("SELECT Participant AS P FROM Participant WHERE (P.company.id = :companyId) AND " +
            "(lower(P.name) like lower(concat('%', :criteria, '%'))) ORDER BY P.name")
    Page<Participant> findByCriteria(Integer companyId, String criteria, Pageable page);
}