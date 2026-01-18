package ru.avg.server.controller.web.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.company.CompanyDto;
import ru.avg.server.model.dto.company.NewCompanyDto;
import ru.avg.server.service.company.CompanyService;

import java.util.Collection;

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
     * Retrieves a collection of all existing companies.
     * <p>
     * This endpoint returns a list of all companies stored in the system. It is used to
     * obtain an overview of all available companies for administrative or selection purposes.
     * The operation is read-only and does not modify any data.
     * </p>
     *
     * @return a ResponseEntity containing a collection of {@link CompanyDto} objects
     * representing all companies, with HTTP status 200 (OK)
     * @see CompanyService#findAll()
     * @see CompanyDto
     */
    @Operation(summary = "Get all companies", description = "Retrieves a collection of all existing companies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of companies")
    })
    @GetMapping
    public ResponseEntity<Collection<CompanyDto>> findAll() {
        log.debug("Finding all companies");
        Collection<CompanyDto> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    /**
     * Searches for companies based on the provided search criteria.
     * <p>
     * This endpoint performs a case-insensitive partial match search across company data,
     * specifically targeting the company title (name) and INN (Individual Taxpayer Number).
     * If the criteria parameter is not provided or is empty, all companies may be returned
     * depending on service implementation.
     * </p>
     * <p>
     * The search is typically used in UI components such as autocomplete fields or search tables,
     * allowing users to locate companies by name or tax identifier. The operation is read-only
     * and does not modify any data.
     * </p>
     *
     * @param criteria the search string to match against company titles and INNs;
     *                 optional, defaults to empty string when not provided
     * @return a ResponseEntity containing a collection of {@link CompanyDto} objects
     * representing companies that match the search criteria, with HTTP status 200 (OK)
     * @see CompanyService#findByCriteria(String)
     * @see CompanyDto
     */
    @Operation(summary = "Search companies by criteria",
            description = "Finds companies by partial match on title or INN (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of matching companies")
    })
    @GetMapping
    public ResponseEntity<Collection<CompanyDto>> findByCriteria(
            @RequestParam(required = false, defaultValue = "") String criteria) {
        log.debug("Finding company by criteria: {}", criteria);
        Collection<CompanyDto> companies = companyService.findByCriteria(criteria);
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