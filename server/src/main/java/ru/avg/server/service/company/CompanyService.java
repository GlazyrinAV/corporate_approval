package ru.avg.server.service.company;

import org.springframework.data.domain.Page;
import ru.avg.server.model.dto.company.CompanyDto;
import ru.avg.server.model.dto.company.NewCompanyDto;

/**
 * Service interface defining operations for managing company entities within the application.
 * This interface provides methods to perform CRUD (Create, Read, Update, Delete) operations
 * on companies, abstracting the underlying business logic and data access mechanisms.
 * <p>
 * Implementations of this interface are responsible for enforcing business rules,
 * handling transactions, and coordinating with repositories and mappers to persist
 * and retrieve company data.
 * </p>
 *
 * @author AVG
 * @see ru.avg.server.service.company.impl.CompanyServiceImpl
 * @since 1.0
 */
public interface CompanyService {

    /**
     * Saves a new company based on the provided data transfer object.
     * <p>
     * This method is responsible for converting the DTO into a domain entity,
     * validating required fields (e.g., INN format, company type), and persisting
     * the new company record in the database. On success, it returns the saved
     * company data including any system-generated values such as the unique ID.
     * </p>
     *
     * @param newCompanyDto the data transfer object containing company information to be saved;
     *                      must not be {@code null}, and must pass validation constraints
     *                      defined in {@link ru.avg.server.model.dto.company.NewCompanyDto}
     * @return the saved {@link CompanyDto} instance with generated fields populated,
     * such as {@code id}, and all validated and normalized data
     * @throws IllegalArgumentException                            if {@code newCompanyDto} is {@code null}
     * @throws ru.avg.server.exception.company.CompanyTypeNotFound if the specified company type is not supported
     * @see ru.avg.server.model.dto.company.NewCompanyDto
     * @see ru.avg.server.model.company.Company
     */
    CompanyDto save(NewCompanyDto newCompanyDto);

    /**
     * Updates an existing company with the provided data using partial update semantics (PATCH).
     * <p>
     * Only non-null and non-blank fields from the {@code updatedCompanyDto} are applied
     * to the existing company entity. This allows clients to update specific attributes
     * without submitting the full resource representation.
     * </p>
     *
     * @param companyId         the unique identifier of the company to update; must be a positive integer
     * @param updatedCompanyDto the data transfer object containing updated company information;
     *                          must not be {@code null}
     * @return the updated {@link CompanyDto} instance reflecting the changes made in the database
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given {@code companyId}
     * @throws IllegalArgumentException                        if {@code updatedCompanyDto} is {@code null}
     * @see ru.avg.server.model.dto.company.NewCompanyDto
     * @see #findById(Integer)
     */
    CompanyDto update(Integer companyId, NewCompanyDto updatedCompanyDto);

    /**
     * Deletes a company identified by its unique identifier.
     * <p>
     * This operation is idempotent — if the company does not exist, calling this method
     * will not result in an error. However, current implementation does throw
     * {@link ru.avg.server.exception.company.CompanyNotFound} if the company is not found,
     * which should be clarified based on desired behavior.
     * </p>
     *
     * @param companyId the unique identifier of the company to delete; must be a positive integer
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given {@code companyId}
     * @see ru.avg.server.repository.company.CompanyRepository#deleteCompanyById(Integer)
     */
    void delete(Integer companyId);

    /**
     * Retrieves a company by its unique identifier.
     * <p>
     * Fetches the company data from the persistence layer and converts it into a DTO
     * for external use. The returned object includes all available company details.
     * </p>
     *
     * @param companyId the unique identifier of the company to retrieve; must be a positive integer
     * @return the {@link CompanyDto} instance representing the found company
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given {@code companyId}
     * @see #findAll(Integer, Integer)
     * @see ru.avg.server.model.dto.company.mapper.CompanyMapper#toDto(ru.avg.server.model.company.Company)
     */
    CompanyDto findById(Integer companyId);

    /**
     * Retrieves a paginated list of all existing companies.
     * <p>
     * This method returns a page of company data transfer objects (DTOs) based on the provided
     * pagination parameters. It supports large datasets by allowing clients to request specific
     * pages of results using zero-based page numbering and a configurable page size.
     * </p>
     * <p>
     * The actual data retrieval and pagination are handled at the database level to ensure
     * efficient query execution and optimal performance.
     * </p>
     *
     * @param page  the zero-based page number to retrieve; must be non-negative
     * @param limit the maximum number of elements to return per page; must be between 1 and 50 (inclusive)
     * @return a {@link Page} of {@link CompanyDto} objects containing the companies for the requested page,
     * including full pagination metadata such as total elements, total pages, current page number, and page size
     * @throws IllegalArgumentException if page is negative or limit is not in the valid range (1-50)
     * @see Page
     * @see CompanyDto
     */
    Page<CompanyDto> findAll(Integer page, Integer limit);

    /**
     * Searches for companies based on a given criteria with pagination support.
     * <p>
     * This method performs a case-insensitive partial match search on company data,
     * specifically targeting fields such as company title and INN (Individual Taxpayer Number).
     * The search results are returned in a paginated format to support efficient handling
     * of large datasets.
     * </p>
     * <p>
     * Pagination is applied using page-based indexing: the {@code page} parameter specifies
     * the zero-based page number to retrieve, and {@code limit} defines the maximum number
     * of elements per page. The result includes full pagination metadata such as total
     * number of elements, total pages, current page number, and page size.
     * </p>
     *
     * @param criteria the search string to match against company titles and INNs;
     *                 if null or blank, the behavior is defined by the implementation
     *                 (typically returns an empty page)
     * @param page     the zero-based page number to retrieve; must be non-negative
     * @param limit    the maximum number of elements to return per page; must be between 1 and 50 (inclusive)
     * @return a {@link Page} of {@link CompanyDto} objects containing the companies that match
     * the search criteria for the requested page, including full pagination metadata
     * @throws IllegalArgumentException if page is negative or limit is not in the valid range (1-50)
     * @see Page
     * @see CompanyDto
     * @see ru.avg.server.service.company.impl.CompanyServiceImpl#findByCriteria(String, Integer, Integer)
     */
    Page<CompanyDto> findByCriteria(String criteria, Integer page, Integer limit);
}