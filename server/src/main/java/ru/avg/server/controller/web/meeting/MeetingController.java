package ru.avg.server.controller.web.meeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
import ru.avg.server.service.meeting.MeetingService;

import java.util.List;

/**
 * REST controller for managing meetings within a company context.
 * All endpoints are scoped by {companyId} to ensure resource ownership and access control.
 * Provides standard CRUD operations for meetings including creation, retrieval, partial update, and deletion.
 * <p>
 * This controller handles HTTP requests related to meeting management and delegates business logic
 * to the {@link MeetingService}. It uses Spring's {@link RestController} annotation to indicate
 * that this class handles RESTful web services. The {@link RequestMapping} annotation defines
 * the base path for all endpoints in this controller, ensuring all operations are scoped to
 * a specific company.
 * </p>
 * <p>
 * The controller includes comprehensive logging via {@link Slf4j} annotation to aid in monitoring
 * and debugging. Different log levels are used appropriately: INFO for mutation operations
 * (POST, PATCH, DELETE) and DEBUG for query operations (GET). This helps in tracking system
 * activity without overwhelming the logs with read operations.
 * </p>
 *
 * @see MeetingService
 * @author AVG
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    /**
     * Service responsible for handling business logic related to meeting management.
     * This dependency is injected by Spring using constructor injection (enabled by {@link RequiredArgsConstructor})
     * and provides all necessary operations for creating, reading, updating, and deleting meetings.
     * <p>
     * The service encapsulates data access, validation, and business rules, allowing this controller
     * to focus solely on request handling, parameter binding, and response construction.
     * </p>
     *
     * @see MeetingService
     */
    private final MeetingService meetingService;

    /**
     * Retrieves all meetings associated with a specific company.
     * This endpoint returns a list of all meetings for the given company identifier.
     *
     * @param companyId the ID of the company for which to retrieve all meetings
     * @return ResponseEntity containing a list of MeetingDto objects with HTTP status 200 OK
     */
    @Operation(summary = "Get all meetings", description = "Retrieves all meetings associated with a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of meetings")
    })
    @GetMapping
    public ResponseEntity<List<MeetingDto>> findAll(@PathVariable Integer companyId) {
        log.debug("Fetching all meetings for companyId: {}", companyId);
        List<MeetingDto> meetings = meetingService.findAll(companyId);
        return ResponseEntity.ok(meetings);
    }

    /**
     * Retrieves a specific meeting by its unique identifier within the context of a company.
     * The company ID is used for routing and access control purposes.
     *
     * @param companyId the ID of the company to which the meeting belongs
     * @param meetingId the ID of the meeting to retrieve
     * @return ResponseEntity containing the requested MeetingDto with HTTP status 200 OK
     */
    @Operation(summary = "Get meeting by ID", description = "Retrieves a specific meeting by its unique identifier within the context of a company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the meeting"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDto> findById(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.debug("Fetching meeting with meetingId: {} for companyId: {}", meetingId, companyId);
        MeetingDto meeting = meetingService.findById(companyId, meetingId);
        return ResponseEntity.ok(meeting);
    }

    /**
     * Creates a new meeting for a specific company.
     * The meeting data is validated using Jakarta Validation annotations before persistence.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     *
     * @param companyId     the ID of the company for which the meeting is being created
     * @param newMeetingDto the NewMeetingDto containing the data for the new meeting
     * @return ResponseEntity containing the created MeetingDto with assigned ID and HTTP status 201 Created
     */
    @Operation(summary = "Create new meeting", description = "Creates a new meeting for a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meeting created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<MeetingDto> save(
            @PathVariable Integer companyId,
            @Valid @RequestBody NewMeetingDto newMeetingDto) {
        log.info("Creating new meeting for companyId: {}: {}", companyId, newMeetingDto);
        MeetingDto savedMeeting = meetingService.save(companyId, newMeetingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMeeting);
    }

    /**
     * Partially updates an existing meeting identified by its ID within the context of a company.
     * Only the fields provided in the request body will be updated.
     * The input is validated before processing.
     *
     * @param companyId     the ID of the company to which the meeting belongs
     * @param meetingId     the ID of the meeting to update
     * @param newMeetingDto the NewMeetingDto containing the fields to update
     * @return ResponseEntity containing the updated MeetingDto with HTTP status 200 OK
     */
    @Operation(summary = "Update meeting", description = "Partially updates an existing meeting identified by its ID within the context of a company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting updated successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PatchMapping("/{meetingId}")
    public ResponseEntity<MeetingDto> update(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @Valid @RequestBody NewMeetingDto newMeetingDto) {
        log.info("Updating meeting with meetingId: {} for companyId: {}: {}", meetingId, companyId, newMeetingDto);
        MeetingDto updatedMeeting = meetingService.update(companyId, meetingId, newMeetingDto);
        return ResponseEntity.ok(updatedMeeting);
    }

    /**
     * Deletes a meeting identified by its ID within the context of a company.
     * After successful deletion, returns HTTP 204 No Content.
     *
     * @param companyId the ID of the company to which the meeting belongs
     * @param meetingId the ID of the meeting to delete
     * @return ResponseEntity with no content and HTTP status 204 No Content
     */
    @Operation(summary = "Delete meeting", description = "Deletes a meeting identified by its ID within the context of a company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meeting deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> remove(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.info("Deleting meeting with meetingId: {} for companyId: {}", meetingId, companyId);
        meetingService.delete(companyId, meetingId);
        return ResponseEntity.noContent().build();
    }
}