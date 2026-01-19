package ru.avg.server.repository.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.avg.server.model.company.Company;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries on {@link Company} entities.
 * Provides data access methods for managing companies in the database.
 * <p>
 * This interface extends {@link JpaRepository} to inherit standard database operations such as save, findById, delete, etc.
 * It also defines additional custom methods specific to business requirements.
 * </p>
 * <p>
 * The repository is annotated with {@link Repository} to indicate that it's a Spring Data repository
 * and to enable component scanning, exception translation, and proper integration with the Spring context.
 * </p>
 *
 * @see JpaRepository
 * @see Company
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    /**
     * Deletes a company from the database by its unique identifier.
     * <p>
     * This method performs a direct deletion using a JPQL query.
     * It is annotated with {@link Modifying} to indicate that it changes the data state.
     * </p>
     *
     * @param id the unique identifier of the company to delete; must not be null
     * @since 1.0
     */
    @Modifying
    void deleteById(Integer id);

    /**
     * Retrieves a company based on its INN (Individual Taxpayer Number).
     * <p>
     * The INN is a unique tax identification number used in Russia, and this method assumes it is unique
     * within the system. It returns an {@link Optional} to safely handle cases when no company is found.
     * </p>
     *
     * @param inn the INN of the company to find; must not be null
     * @return an {@link Optional} containing the found {@link Company} if present, or an empty {@link Optional} if not found
     * @throws IllegalArgumentException if the provided INN is null
     * @since 1.0
     */
    Optional<Company> findByInn(Long inn);

    /**
     * Finds companies that match the specified search criteria with pagination and sorting support.
     * <p>
     * This method performs a case-insensitive partial match search on two key company attributes:
     * the company title (name) and the INN (Individual Taxpayer Number). The search uses the SQL LIKE
     * operator with wildcards on both sides ('%criteria%') to find matches anywhere within the field.
     * </p>
     * <p>
     * Results are filtered to include only those companies where either the title or INN contains
     * the search string (case-insensitive). The results are then sorted alphabetically by title
     * in ascending order (A to Z).
     * </p>
     * <p>
     * Pagination is applied at the database level through the {@link Pageable} parameter,
     * ensuring efficient retrieval and preventing performance issues with large datasets.
     * The method returns a {@link Page} object that contains both the subset of entities
     * for the requested page and metadata such as total element count and number of pages.
     * </p>
     *
     * @param criteria the search string to match against company title and INN fields;
     *                 must not be null, though empty or blank values may be handled by the caller
     * @param page     the pagination information including page number (zero-based) and page size;
     *                 must not be {@code null}
     * @return a {@link Page} of {@link Company} entities matching the search criteria,
     * sorted by title in ascending order, with full pagination metadata
     * @see Page
     * @see Pageable
     * @see Company
     */
    @Query("SELECT c FROM Company AS c WHERE " +
            "(lower(c.title) like lower(concat('%', :criteria, '%'))) OR " +
            "(cast(c.inn AS STRING) like concat('%', :criteria, '%')) ORDER BY c.title")
    Page<Company> findByCriteria(@Param("criteria") String criteria, Pageable page);

    /**
     * Retrieves a paginated list of all companies sorted by title in ascending order.
     * <p>
     * This method fetches companies from the database and sorts them case-sensitively
     * by the {@code title} field in ascending (A-Z) order. The pagination is applied
     * at the database level to ensure efficient retrieval and prevent loading
     * large datasets into memory.
     * </p>
     * <p>
     * The result is returned as a {@link Page} object, which includes not only
     * the list of companies for the requested page but also metadata such as
     * total number of elements, total pages, current page number, and page size.
     * </p>
     *
     * @param page the pagination information including page number (zero-based)
     *             and page size; must not be {@code null}
     * @return a {@link Page} of {@link Company} entities containing the companies
     * for the requested page, sorted by title in ascending order
     * @see Page
     * @see Pageable
     * @see Company
     */
    Page<Company> findAllByOrderByTitleAsc(Pageable page);
}