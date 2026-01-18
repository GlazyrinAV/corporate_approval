package ru.avg.server.controller.web.participant;

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
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.participant.NewParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.service.participant.ParticipantService;

/**
 * REST controller for managing participants within a company context.
 * All operations are scoped by {companyId} to enforce access control and data ownership.
 * Provides standard CRUD endpoints for participant management including creation, retrieval,
 * partial update, and deletion of participants.
 * <p>
 * This controller handles HTTP requests and delegates business logic to {@link ParticipantService}.
 * It includes comprehensive logging for monitoring and debugging purposes, with different log levels
 * for different types of operations (info for mutations, debug for queries, warn for deletions).
 * </p>
 *
 * @author AVG
 * @see ParticipantService
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/{companyId}/participant")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    /**
     * Service responsible for handling business logic related to participant management.
     * This dependency is injected by Spring using constructor injection (enabled by {@link RequiredArgsConstructor})
     * and provides all necessary operations for creating, reading, updating, and deleting participants.
     * <p>
     * The service encapsulates data access, validation, and business rules, allowing this controller
     * to focus solely on request handling, parameter binding, and response construction.
     * </p>
     *
     * @see ParticipantService
     */
    private final ParticipantService participantService;

    /**
     * Retrieves a paginated list of all participants belonging to a specific company.
     * <p>
     * This endpoint returns a subset of participants based on the provided pagination parameters.
     * It supports large datasets by allowing clients to request specific pages of results.
     * The operation is read-only and does not modify any data.
     * </p>
     * <p>
     * The response includes full pagination metadata such as total elements, total pages,
     * current page number, and page size, enabling clients to navigate through the dataset effectively.
     * </p>
     *
     * @param companyId the unique identifier of the company whose participants are to be retrieved;
     *                  must correspond to an existing company
     * @param page      the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit     the maximum number of elements to return per page; must be between 1 and 20 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link ParticipantDto} objects representing
     * the requested page of participants, with HTTP status 200 (OK)
     * @see ParticipantService#findAll(Integer, Integer, Integer)
     * @see ParticipantDto
     * @see Page
     */
    @Operation(summary = "Get all participants of a company with pagination",
            description = "Retrieves a paginated list of all participants belonging to a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of participants"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters (page < 0 or limit not in 1-20 range)"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping
    public ResponseEntity<Page<ParticipantDto>> findAll(@PathVariable Integer companyId,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer page,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer limit) {
        log.debug("Request to get all participants for companyId={}", companyId);
        Page<ParticipantDto> participants = participantService.findAll(companyId, page, limit);
        return ResponseEntity.ok(participants);
    }

    /**
     * Creates a new participant within the specified company.
     * The participant data is validated using Jakarta Validation annotations before being persisted.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     *
     * @param companyId         the ID of the company for which the participant is being created
     * @param newParticipantDto the NewParticipantDto containing the data for the new participant
     * @return ResponseEntity with HTTP 201 CREATED status and the saved ParticipantDto including generated ID
     */
    @Operation(summary = "Create participant", description = "Creates a new participant within the specified company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Participant created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ParticipantDto> save(
            @PathVariable Integer companyId,
            @Valid @RequestBody NewParticipantDto newParticipantDto) {
        log.info("Request to create participant for companyId={}: {}", companyId, newParticipantDto);
        ParticipantDto savedParticipant = participantService.save(companyId, newParticipantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
    }

    /**
     * Retrieves a specific participant by their unique identifier within the context of a company.
     * The company ID is used for routing and access control, ensuring proper scoping.
     *
     * @param companyId     the ID of the company to which the participant belongs
     * @param participantId the ID of the participant to retrieve
     * @return ResponseEntity with HTTP 200 OK status and the requested ParticipantDto
     */
    @Operation(summary = "Get participant by ID", description = "Retrieves a specific participant by their unique identifier within the context of a company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the participant"),
            @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @GetMapping("/{participantId}")
    public ResponseEntity<ParticipantDto> findById(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId) {
        log.debug("Request to find participant by id={} for companyId={}", participantId, companyId);
        ParticipantDto participantDto = participantService.findById(companyId, participantId);
        return ResponseEntity.ok(participantDto);
    }

    /**
     * Partially updates an existing participant identified by ID within the company context.
     * Only the fields provided in the request body will be updated, leaving others unchanged.
     * The input is validated before processing.
     *
     * @param companyId         the ID of the company to which the participant belongs
     * @param participantId     the ID of the participant to update
     * @param newParticipantDto the NewParticipantDto containing the fields to update
     * @return ResponseEntity with HTTP 200 OK status and the updated ParticipantDto
     */
    @Operation(summary = "Update participant", description = "Partially updates an existing participant identified by ID within the company context")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PatchMapping("/{participantId}")
    public ResponseEntity<ParticipantDto> update(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId,
            @Valid @RequestBody NewParticipantDto newParticipantDto) {
        log.info("Request to update participant id={} for companyId={}: {}", participantId, companyId, newParticipantDto);
        ParticipantDto updatedParticipant = participantService.update(companyId, participantId, newParticipantDto);
        return ResponseEntity.ok(updatedParticipant);
    }

    /**
     * Deletes a participant identified by their ID within the company context.
     * This is a soft or hard delete operation depending on the service implementation.
     * After successful deletion, returns no content.
     *
     * @param companyId     the ID of the company to which the participant belongs
     * @param participantId the ID of the participant to delete
     * @return ResponseEntity with HTTP 204 NO_CONTENT status and nobody
     */
    @Operation(summary = "Delete participant", description = "Deletes a participant identified by their ID within the company context")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Participant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeById(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId) {
        log.warn("Request to delete participant id={} for companyId={}", participantId, companyId);
        participantService.delete(companyId, participantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for participants within a specific company based on search criteria with pagination support.
     * <p>
     * This endpoint performs a case-insensitive partial match search on participant data (e.g., name, position)
     * that belongs to the specified company. The results are returned in a paginated format to support
     * efficient handling of large datasets and improve performance.
     * </p>
     * <p>
     * If the search criteria is empty or not provided, the method returns a paginated list of all participants
     * belonging to the company (subject to pagination). The maximum length of the search string is limited
     * to 50 characters to prevent abuse and ensure system stability.
     * </p>
     *
     * @param companyId the unique identifier of the company to which participants must belong;
     *                  must be a valid positive integer
     * @param criteria  the search string to match against participant names or other relevant fields;
     *                  optional, defaults to empty string if not provided;
     *                  maximum length is 50 characters
     * @param page      the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit     the maximum number of elements to return per page; must be between 1 and 20 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link ParticipantDto} objects representing
     * participants matching the search criteria within the specified company, including full
     * pagination metadata (total elements, total pages, etc.), with HTTP status 200 (OK)
     * @see ParticipantService#findByCriteria(Integer, String, Integer, Integer)
     * @see ParticipantDto
     */
    @Operation(summary = "Search participants by criteria within a company",
            description = "Finds participants in a specific company by partial match on name or other fields with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of matching participants"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters (page < 0 or limit not in 1-20 range) or criteria exceeds 50 characters"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ParticipantDto>> findByCriteria(
            @PathVariable Integer companyId,
            @RequestParam(required = false, defaultValue = "") @Size(max = 50) String criteria,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer limit) {
        log.debug("Request to find participants by criteria={} for companyId={} with page={} and limit={}",
                criteria, companyId, page, limit);
        return ResponseEntity.ok(participantService.findByCriteria(companyId, criteria, page, limit));
    }
}