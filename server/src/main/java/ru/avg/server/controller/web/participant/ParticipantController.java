package ru.avg.server.controller.web.participant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.service.participant.ParticipantService;

import java.util.List;

/**
 * REST controller for managing participants within a company context.
 * All operations are scoped by {companyId} to enforce access control and data ownership.
 * Provides standard CRUD endpoints for participant management including creation, retrieval,
 * partial update, and deletion of participants.
 */
@RestController
@RequestMapping("/approval/{companyId}/participant")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * Retrieves all participants associated with a specific company.
     * This endpoint returns a complete list of participants for the given company.
     *
     * @param companyId the ID of the company for which to retrieve all participants
     * @return ResponseEntity with HTTP 200 OK status containing a list of ParticipantDto objects
     */
    @Operation(summary = "Get all participants", description = "Retrieves all participants associated with a specific company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of participants")
    })
    @GetMapping
    public ResponseEntity<List<ParticipantDto>> findAll(@PathVariable Integer companyId) {
        log.debug("Request to get all participants for companyId={}", companyId);
        List<ParticipantDto> participants = participantService.findAll(companyId);
        return ResponseEntity.ok(participants);
    }

    /**
     * Creates a new participant within the specified company.
     * The participant data is validated using Jakarta Validation annotations before being persisted.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     *
     * @param companyId      the ID of the company for which the participant is being created
     * @param participantDto the ParticipantDto containing the data for the new participant
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
            @Valid @RequestBody ParticipantDto participantDto) {
        log.info("Request to create participant for companyId={}: {}", companyId, participantDto);
        ParticipantDto savedParticipant = participantService.save(companyId, participantDto);
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
     * @param newParticipantDto the ParticipantDto containing the fields to update
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
            @Valid @RequestBody ParticipantDto newParticipantDto) {
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
}