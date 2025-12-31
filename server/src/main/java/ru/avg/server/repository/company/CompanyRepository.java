package ru.avg.server.repository.company;

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
     * @return the number of deleted entities (typically 0 if not found, 1 if successfully deleted)
     * @throws IllegalArgumentException if the provided ID is null
     *
     * @since 1.0
     */
    @Modifying
    @Query("DELETE FROM Company c WHERE c.id = :id")
    int deleteCompanyById(@Param("id") Integer id);

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
     *
     * @since 1.0
     */
    Optional<Company> findByInn(Long inn);
}