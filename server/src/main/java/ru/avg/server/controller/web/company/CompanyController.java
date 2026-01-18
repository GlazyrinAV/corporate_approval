package ru.avg.server.controller.web.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.company.CompanyDto;
import ru.avg.server.model.dto.company.NewCompanyDto;
import ru.avg.server.service.company.CompanyService;

/**
 * REST controller for managing company resources.
 * Provides CRUD operations with proper HTTP semantics and logging.
 * All endpoints are mapped under the base path "/approval/company".
 * <p>
 * This controller handles incoming HTTP requests, performs request validation,
 * delegates business logic to {@link CompanyService}, and returns appropriate
 * HTTP responses. It includes comprehensive logging for monitoring and debugging.
 * </p>
 *
 * @author AVG
 * @see CompanyService
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/company")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompanyController {

    /**
     * Service instance responsible for handling business logic related to companies.
     * Injected via constructor by Spring due to {@link RequiredArgsConstructor}.
     * Used to perform operations such as saving, updating, retrieving, and deleting companies.
     */
    private final CompanyService companyService;

    /**
     * Creates a new company based on the provided {@link NewCompanyDto}.
     * The input is validated using Jakarta Validation annotations before processing.
     * If validation fails, a {@link org.springframework.web.bind.MethodArgumentNotValidException}
     * will be automatically thrown and handled by the global exception handler.
     *
     * @param newCompanyDto the DTO containing the data for the new company, must not be {@code null}
     * @return {@link ResponseEntity} containing the saved {@link CompanyDto} with HTTP status {@code 201 Created}
     * @throws jakarta.validation.ConstraintViolationException if validation constraints are violated
     * @apiNote This endpoint supports full creation of a new company. The generated ID and other system-assigned
     * fields will be included in the response body.
     * @see CompanyService#save(NewCompanyDto)
     */
    @Operation(summary = "Create new company", description = "Creates a new company based on the provided CompanyDto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CompanyDto> save(@RequestBody @Valid NewCompanyDto newCompanyDto) {
        log.debug("Saving new Company: {}", newCompanyDto);
        CompanyDto savedCompany = companyService.save(newCompanyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param companyId the ID of the company to retrieve, must be a positive integer
     * @return {@link ResponseEntity} containing the {@link CompanyDto} with HTTP status {@code 200 OK}
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given ID
     * @see CompanyService#findById(Integer)
     */
    @Operation(summary = "Get company by ID", description = "Retrieves a company by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the company"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> findById(@PathVariable Integer companyId) {
        log.debug("Finding company by companyId: {}", companyId);
        CompanyDto companyDto = companyService.findById(companyId);
        return ResponseEntity.ok(companyDto);
    }

    /**
     * Retrieves a paginated list of all companies.
     * <p>
     * This endpoint returns a subset of companies based on the provided pagination parameters.
     * It supports large datasets by allowing clients to request specific pages of results.
     * The operation is read-only and does not modify any data.
     * </p>
     * <p>
     * The response includes full pagination metadata such as total elements, total pages,
     * current page number, and page size, enabling clients to navigate through the dataset effectively.
     * Results are sorted by company title in ascending order.
     * </p>
     *
     * @param page  the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit the maximum number of elements to return per page; must be between 1 and 50 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link CompanyDto} objects representing
     * the requested page of companies, with HTTP status 200 (OK)
     * @see CompanyService#findAll(Integer, Integer)
     * @see CompanyDto
     * @see Page
     */
    @Operation(summary = "Get all companies with pagination",
            description = "Retrieves a paginated list of all existing companies with full pagination metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of companies"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters (page < 0 or limit not in 1-50 range)")
    })
    @GetMapping
    public ResponseEntity<Page<CompanyDto>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        log.debug("Fetching companies - page: {}, limit: {}", page, limit);
        Page<CompanyDto> companies = companyService.findAll(page, limit);
        return ResponseEntity.ok(companies);
    }

    /**
     * Searches for companies based on a given criteria with pagination support.
     * <p>
     * This endpoint performs a case-insensitive partial match search on company data,
     * specifically targeting fields such as company title and INN (Individual Taxpayer Number).
     * The search results are returned in a paginated format to support efficient handling
     * of large datasets and improve performance.
     * </p>
     * <p>
     * If the search criteria is empty or not provided, the method returns an empty page
     * to prevent unintended retrieval of all company records. The maximum length of the
     * search string is limited to 100 characters to prevent abuse and ensure system stability.
     * </p>
     *
     * @param criteria the search string to match against company titles and INNs;
     *                 optional, defaults to empty string if not provided;
     *                 maximum length is 100 characters
     * @param page     the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit    the maximum number of elements to return per page; must be between 1 and 50 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link CompanyDto} objects representing
     * the companies matching the search criteria for the requested page,
     * including full pagination metadata (total elements, total pages, etc.),
     * with HTTP status 200 (OK)
     * @see CompanyService#findByCriteria(String, Integer, Integer)
     * @see CompanyDto
     * @see Page
     */
    @Operation(summary = "Search companies by criteria with pagination",
            description = "Finds companies by partial match on title or INN (case-insensitive) with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of matching companies"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters (page < 0 or limit not in 1-50 range) or criteria exceeds 100 characters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<CompanyDto>> findByCriteria(
            @RequestParam(required = false, defaultValue = "") @Size(max = 100) String criteria,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        log.debug("Finding companies by criteria with pagination(page: {}, limit: {}): {}", page, limit, criteria);
        Page<CompanyDto> companies = companyService.findByCriteria(criteria, page, limit);
        return ResponseEntity.ok(companies);
    }

    /**
     * Partially updates an existing company using the provided fields in {@link NewCompanyDto}.
     * Only the fields present in the request body will be updated (PATCH semantics).
     * The input is validated before processing.
     *
     * @param companyId         the ID of the company to update, must be a positive integer
     * @param updatedCompanyDto the DTO containing the fields to update, must not be {@code null}
     * @return {@link ResponseEntity} containing the updated {@link CompanyDto} with HTTP status {@code 200 OK}
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given ID
     * @throws jakarta.validation.ConstraintViolationException if validation constraints are violated
     * @apiNote Fields with {@code null} values or blank strings will be ignored during update.
     * This operation performs a partial update, preserving existing values for unspecified fields.
     * @see CompanyService#update(Integer, NewCompanyDto)
     */
    @Operation(summary = "Update company", description = "Partially updates an existing company using the provided fields in CompanyDto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PatchMapping("/{companyId}")
    public ResponseEntity<CompanyDto> update(
            @PathVariable Integer companyId,
            @Valid @RequestBody NewCompanyDto updatedCompanyDto) {
        log.debug("Editing company by companyId: {}", companyId);
        CompanyDto updatedCompany = companyService.update(companyId, updatedCompanyDto);
        return ResponseEntity.ok(updatedCompany);
    }

    /**
     * Deletes a company identified by its ID.
     * After successful deletion, returns HTTP {@code 204 No Content}.
     *
     * @param companyId the ID of the company to delete, must be a positive integer
     * @return {@link ResponseEntity} with no content and HTTP status {@code 204 No Content}
     * @throws ru.avg.server.exception.company.CompanyNotFound if no company exists with the given ID
     * @apiNote The response body is empty upon successful deletion.
     * @see CompanyService#delete(Integer)
     */
    @Operation(summary = "Delete company", description = "Deletes a company identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> delete(@PathVariable Integer companyId) {
        log.debug("Deleting company by companyId: {}", companyId);
        companyService.delete(companyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}