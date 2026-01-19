package ru.avg.server.repository.meeting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for managing {@link Meeting} entities.
 * Provides CRUD operations and custom query methods for meeting-related data access.
 * <p>
 * This repository extends {@link JpaRepository} to inherit standard database operations
 * and defines additional business-specific queries for efficient data retrieval.
 * </p>
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    /**
     * Finds a meeting by company ID, meeting type, and date.
     * Used to ensure uniqueness of meetings with the same attributes.
     *
     * @param companyId the ID of the company organizing the meeting; must not be null
     * @param type      the type of the meeting (e.g., Annual, Extraordinary); must not be null
     * @param date      the date when the meeting takes place; must not be null
     * @return an {@link Optional} containing the found meeting, or empty if not found
     * @since 1.0
     */
    Optional<Meeting> findByCompanyIdAndTypeAndDate(Integer companyId, MeetingType type, LocalDate date);

    /**
     * Retrieves a paginated list of meetings associated with a specific company,
     * sorted by the meeting date in descending order (newest first).
     * <p>
     * This method uses Spring Data JPA query derivation to automatically generate
     * the appropriate JPQL query based on the method name. It filters meetings
     * by the given company ID and orders the results by the {@code date} field
     * of the {@link Meeting} entity in descending order.
     *
     * @param companyId the unique identifier of the company whose meetings are to be retrieved;
     *                  must not be {@code null}
     * @param pageable  the pagination information containing page number, page size,
     *                  and sorting direction; must not be {@code null}
     * @return a {@link Page} containing the requested slice of {@link Meeting} entities
     * that belong to the specified company, sorted by date (newest first);
     * never {@code null}
     * @see Meeting
     * @see Page
     * @see Pageable
     * @see org.springframework.data.jpa.repository.JpaRepository
     */
    Page<Meeting> findByCompanyIdOrderByDateDesc(Integer companyId, Pageable pageable);

    /**
     * Counts the number of meetings of a specific type within a given date range.
     *
     * @param type      the type of meetings to count
     * @param startDate the start of the date range (inclusive)
     * @param endDate   the end of the date range (inclusive)
     * @return the number of matching meetings
     * @since 1.0
     */
    @Query("SELECT COUNT(m) FROM Meeting m WHERE m.type = :type AND m.date BETWEEN :startDate AND :endDate")
    long countByTypeAndDateBetween(
            @Param("type") MeetingType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Finds the most recent meeting of a specific type for a company.
     *
     * @param companyId the ID of the company
     * @param type      the type of meeting
     * @return an {@link Optional} containing the most recent meeting, or empty if not found
     * @since 1.0
     */
    @Query("SELECT m FROM Meeting m WHERE m.company.id = :companyId AND m.type = :type ORDER BY m.date DESC")
    Optional<Meeting> findFirstByCompanyIdAndTypeOrderByDateDesc(
            @Param("companyId") Integer companyId,
            @Param("type") MeetingType type
    );
}