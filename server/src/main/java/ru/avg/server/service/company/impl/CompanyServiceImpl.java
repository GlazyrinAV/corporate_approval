package ru.avg.server.service.company.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.dto.company.CompanyDto;
import ru.avg.server.model.dto.company.NewCompanyDto;
import ru.avg.server.model.dto.company.mapper.CompanyMapper;
import ru.avg.server.model.dto.company.mapper.NewCompanyMapper;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.service.company.CompanyService;
import ru.avg.server.utils.updater.Updater;
import ru.avg.server.utils.verifier.Verifier;

/**
 * Implementation of {@link CompanyService} providing business logic for managing company entities.
 * This service handles creation, retrieval, update, and deletion of companies using database operations
 * via {@link CompanyRepository} and DTO mapping via {@link CompanyMapper}.
 *
 * <p>The class is annotated with {@link Service} to indicate it's a Spring service component,
 * and uses {@link RequiredArgsConstructor} to generate a constructor for dependency injection.</p>
 *
 * <p>This implementation supports partial updates (PATCH semantics), where only non-null or non-blank
 * fields from the update DTO are applied to the existing entity. The actual field-level merging
 * is delegated to the {@link Updater} utility class.</p>
 *
 * @author AVG
 * @see CompanyService
 * @see CompanyRepository
 * @see CompanyMapper
 * @see NewCompanyMapper
 * @see Updater
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    /**
     * Maximum number of companies to return per page in paginated responses.
     * This value is injected from configuration using the property key "page.maxlimit.company"
     * and used to validate the limit parameter in paginated methods. It ensures consistent
     * pagination limits across the company service and prevents excessively large responses.
     * The value is validated by {@link Verifier#verifyPageAndLimit(Integer, Integer, Integer)}.
     *
     * @see #findAll(Integer, Integer)
     * @see #findByCriteria(String, Integer, Integer)
     * @see Verifier#verifyPageAndLimit(Integer, Integer, Integer)
     */
    @Value("${page.maxlimit.company}")
    private Integer pageLimit;

    /**
     * Repository used for data persistence operations on {@link Company} entities.
     * Injected via constructor by Spring due to {@link RequiredArgsConstructor}.
     */
    private final CompanyRepository storage;

    /**
     * Mapper responsible for converting between {@link Company} entities and {@link CompanyDto} objects.
     * Used to transform persistent domain models into data transfer objects for external exposure.
     */
    private final CompanyMapper companyMapper;

    /**
     * Mapper responsible for converting {@link NewCompanyDto} objects into {@link Company} entities.
     * Used when creating new companies from API input data.
     */
    private final NewCompanyMapper newCompanyMapper;

    /**
     * Utility component used to perform partial updates on entity objects.
     * Applies non-null and non-blank fields from the source entity to the target entity.
     */
    private final Updater updater;

    /**
     * Utility component used for verifying access rights, existence of entities,
     * and validating input parameters such as pagination limits.
     */
    private final Verifier verifier;

    /**
     * Saves a new company to the database.
     * <p>Converts the incoming DTO to a domain entity using {@link NewCompanyMapper},
     * persists it via the repository, and returns the saved data as a DTO using {@link CompanyMapper}.</p>
     *
     * @param newCompanyDto the DTO containing company data (must not be null)
     * @return the saved CompanyDto with generated ID and system-assigned fields
     * @throws IllegalArgumentException                            if newCompanyDto is null
     * @throws ru.avg.server.exception.company.CompanyTypeNotFound if the specified company type is not supported
     * @see NewCompanyMapper#fromDto(NewCompanyDto)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto save(NewCompanyDto newCompanyDto) {
        Company company = newCompanyMapper.fromDto(newCompanyDto);
        Company savedCompany = storage.save(company);
        return companyMapper.toDto(savedCompany);
    }

    /**
     * Updates an existing company with non-null or non-blank fields from the provided DTO.
     * Only the fields that are present in the DTO are updated (partial update semantics).
     * <p>The actual field comparison and copying is handled by the {@link Updater} class,
     * which ensures that only meaningful values (non-null, non-blank strings) are applied
     * from the update source to the existing entity.</p>
     *
     * @param id                the ID of the company to update
     * @param updatedCompanyDto the DTO containing updated values (nullable fields are ignored)
     * @return the updated CompanyDto reflecting changes in the database
     * @throws CompanyNotFound          if no company exists with the given ID
     * @throws IllegalArgumentException if updatedCompanyDto is null
     * @see Updater(Company, Company)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto update(Integer id, NewCompanyDto updatedCompanyDto) {
        Company existingCompany = storage.findById(id)
                .orElseThrow(() -> new CompanyNotFound(id));
        Company updateSource = newCompanyMapper.fromDto(updatedCompanyDto);

        Company updatedCompany = updater.update(existingCompany, updateSource);
        return companyMapper.toDto(updatedCompany);
    }

    /**
     * Deletes a company by its ID.
     * Uses a custom repository method to avoid loading the entity first.
     * <p>The operation is wrapped in a transaction to ensure atomicity.
     * First checks if the company exists; if not, throw an exception.
     * Otherwise, performs deletion using a direct delete-by-ID method.</p>
     *
     * @param companyId the ID of the company to delete
     * @throws CompanyNotFound if no company exists with the given ID
     * @see CompanyRepository#existsById(Object)
     */
    @Override
    @Transactional
    public void delete(Integer companyId) {
        if (!storage.existsById(companyId)) {
            throw new CompanyNotFound(companyId);
        }
        storage.deleteById(companyId);
    }

    /**
     * Retrieves a company by its ID.
     * <p>Finds the company in the database and converts it to a DTO for external use.
     * Throws an exception if the company is not found.</p>
     *
     * @param companyId the ID of the company to retrieve
     * @return the corresponding CompanyDto with all available company data
     * @throws CompanyNotFound if no company exists with the given ID
     * @see CompanyRepository#findById(Object)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto findById(Integer companyId) {
        Company company = storage.findById(companyId)
                .orElseThrow(() -> new CompanyNotFound(companyId));
        return companyMapper.toDto(company);
    }

    /**
     * Retrieves a paginated list of all companies sorted by title in ascending order.
     * <p>
     * This method fetches companies from the data source using pagination to support large datasets
     * and improve performance. The actual retrieval and sorting are applied at the database level
     * through the repository layer, ensuring efficient query execution.
     * </p>
     * <p>
     * Pagination is handled using page number and page size (limit) strategy. The {@code page} parameter
     * represents the zero-based page index to retrieve, and {@code limit} defines the maximum number
     * of results per page. This aligns directly with Spring Data's native pagination model.
     * </p>
     * <p>
     * The resulting page of company entities is transformed into a page of data transfer objects (DTOs)
     * using the {@link CompanyMapper#toDto(Company)} function. The mapping preserves all pagination
     * metadata such as total elements, total pages, current page number, and page size.
     * </p>
     *
     * @param page  the zero-based page number to retrieve; must be non-negative
     * @param limit the maximum number of elements to return per page; must be between 1 and 50 (inclusive)
     * @return a {@link Page} of {@link CompanyDto} objects containing all companies for the requested page,
     * including full pagination metadata and sorted by title in ascending order
     * @throws IllegalArgumentException if page is negative or limit is not in valid range (1-50)
     * @see CompanyRepository#findAllByOrderByTitleAsc(Pageable)
     * @see CompanyMapper#toDto(Company)
     * @see PageRequest#of(int, int)
     */
    @Override
    public Page<CompanyDto> findAll(Integer page, Integer limit) {
        verifier.verifyPageAndLimit(page, limit, pageLimit);

        Pageable pageable = PageRequest.of(page, limit);

        // Fetch all companies with pagination and map entities to DTOs preserving metadata
        return storage.findAllByOrderByTitleAsc(pageable).map(companyMapper::toDto);
    }

    /**
     * Retrieves a paginated list of companies that match the specified search criteria.
     * <p>
     * This method performs a case-insensitive partial match search on company data (e.g., title, INN).
     * The search is applied at the database level to ensure efficient execution, especially for large datasets.
     * </p>
     * <p>
     * Pagination is handled using a zero-based page index and a page size (limit). The result includes
     * full pagination metadata such as total number of elements, total pages, current page number, and page size.
     * </p>
     * <p>
     * If the search criteria is {@code null} or blank, the method returns an empty page to prevent
     * unintended retrieval of all company records. For valid criteria, only companies matching the search
     * are returned.
     * </p>
     *
     * @param criteria the search string to match against company fields such as title or INN;
     *                 if {@code null} or blank, an empty page is returned
     * @param page     the zero-based page number to retrieve; must be non-negative
     * @param limit    the maximum number of elements to return per page; must be between 1 and 50 (inclusive)
     * @return a {@link Page} of {@link CompanyDto} objects containing the companies that match the
     * search criteria for the requested page, including full pagination metadata
     * @throws IllegalArgumentException if {@code page} is negative or {@code limit} is not in the valid range (1–50)
     * @see CompanyRepository#findByCriteria(String, Pageable)
     * @see CompanyMapper#toDto(Company)
     * @see PageRequest#of(int, int)
     */
    @Override
    public Page<CompanyDto> findByCriteria(String criteria, Integer page, Integer limit) {
        verifier.verifyPageAndLimit(page, limit, pageLimit);

        // Return empty page for null or blank criteria to prevent unintended full dataset retrieval
        if (criteria == null || criteria.isBlank()) {
            return Page.empty(PageRequest.of(page, limit));
        }

        Pageable pageable = PageRequest.of(page, limit);

        // Delegate to repository for database-level filtering and pagination
        Page<Company> companyPage = storage.findByCriteria(criteria, pageable);

        // Transform entities to DTOs while preserving pagination metadata
        return companyPage.map(companyMapper::toDto);
    }
}