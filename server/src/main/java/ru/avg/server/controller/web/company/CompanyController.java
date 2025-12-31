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
import ru.avg.server.model.dto.CompanyDto;
import ru.avg.server.service.company.CompanyService;

import java.util.List;

/**
 * REST controller for managing company resources.
 * Provides CRUD operations with proper HTTP semantics and logging.
 * All endpoints are mapped under the base path "/approval/company".
 */
@RestController
@RequestMapping("/approval/company")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Creates a new company based on the provided CompanyDto.
     * The input is validated using Jakarta Validation annotations.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     *
     * @param companyDto the CompanyDto containing the data for the new company
     * @return ResponseEntity containing the saved CompanyDto with HTTP status 201 Created
     */
    @Operation(summary = "Create new company", description = "Creates a new company based on the provided CompanyDto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Company created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CompanyDto> save(@RequestBody @Valid CompanyDto companyDto) {
        log.debug("Saving new Company: {}", companyDto);
        CompanyDto savedCompany = companyService.save(companyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param companyId the ID of the company to retrieve
     * @return ResponseEntity containing the CompanyDto with HTTP status 200 OK
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
     * Retrieves a list of all existing companies.
     *
     * @return ResponseEntity containing a list of CompanyDto objects with HTTP status 200 OK
     */
    @Operation(summary = "Get all companies", description = "Retrieves a list of all existing companies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of companies")
    })
    @GetMapping
    public ResponseEntity<List<CompanyDto>> findAll() {
        log.debug("Finding all companies");
        List<CompanyDto> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    /**
     * Partially updates an existing company using the provided fields in CompanyDto.
     * Only the fields present in the request body will be updated.
     * The input is validated before processing.
     *
     * @param companyId the ID of the company to update
     * @param company   the CompanyDto containing the fields to update
     * @return ResponseEntity containing the updated CompanyDto with HTTP status 200 OK
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
            @Valid @RequestBody CompanyDto company) {
        log.debug("Editing company by companyId: {}", companyId);
        CompanyDto updatedCompany = companyService.update(companyId, company);
        return ResponseEntity.ok(updatedCompany);
    }

    /**
     * Deletes a company identified by its ID.
     * After successful deletion, returns HTTP 204 No Content.
     *
     * @param companyId the ID of the company to delete
     * @return ResponseEntity with no content and HTTP status 204 No Content
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