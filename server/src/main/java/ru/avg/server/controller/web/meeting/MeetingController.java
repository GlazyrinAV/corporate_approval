package ru.avg.server.controller.web.meeting;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.service.meeting.MeetingService;

import java.util.List;

/**
 * REST controller for managing meetings within a company context.
 * All endpoints are scoped by {companyId} to ensure resource ownership and access control.
 * Provides standard CRUD operations for meetings including creation, retrieval, partial update, and deletion.
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting")
@AllArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * Retrieves all meetings associated with a specific company.
     * This endpoint returns a list of all meetings for the given company identifier.
     *
     * @param companyId the ID of the company for which to retrieve all meetings
     * @return ResponseEntity containing a list of MeetingDto objects with HTTP status 200 OK
     */
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
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDto> findById(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.debug("Fetching meeting with meetingId: {} for companyId: {}", meetingId, companyId);
        MeetingDto meeting = meetingService.findById(meetingId);
        return ResponseEntity.ok(meeting);
    }

    /**
     * Creates a new meeting for a specific company.
     * The meeting data is validated using Jakarta Validation annotations before persistence.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     *
     * @param companyId  the ID of the company for which the meeting is being created
     * @param meetingDto the MeetingDto containing the data for the new meeting
     * @return ResponseEntity containing the created MeetingDto with assigned ID and HTTP status 201 Created
     */
    @PostMapping
    public ResponseEntity<MeetingDto> save(
            @PathVariable Integer companyId,
            @Valid @RequestBody MeetingDto meetingDto) {
        log.info("Creating new meeting for companyId: {}: {}", companyId, meetingDto);
        MeetingDto savedMeeting = meetingService.save(meetingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMeeting);
    }

    /**
     * Partially updates an existing meeting identified by its ID within the context of a company.
     * Only the fields provided in the request body will be updated.
     * The input is validated before processing.
     *
     * @param companyId     the ID of the company to which the meeting belongs
     * @param meetingId     the ID of the meeting to update
     * @param newMeetingDto the MeetingDto containing the fields to update
     * @return ResponseEntity containing the updated MeetingDto with HTTP status 200 OK
     */
    @PatchMapping("/{meetingId}")
    public ResponseEntity<MeetingDto> update(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @Valid @RequestBody MeetingDto newMeetingDto) {
        log.info("Updating meeting with meetingId: {} for companyId: {}: {}", meetingId, companyId, newMeetingDto);
        MeetingDto updatedMeeting = meetingService.update(meetingId, newMeetingDto);
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
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> remove(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.info("Deleting meeting with meetingId: {} for companyId: {}", meetingId, companyId);
        meetingService.delete(meetingId);
        return ResponseEntity.noContent().build();
    }
}