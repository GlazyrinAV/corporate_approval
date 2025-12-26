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
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting")
@AllArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * Retrieves all meetings for a given company.
     *
     * @param companyId the ID of the company
     * @return list of MeetingDto objects
     */
    @GetMapping
    public ResponseEntity<List<MeetingDto>> findAll(@PathVariable Integer companyId) {
        log.debug("Fetching all meetings for companyId: {}", companyId);
        List<MeetingDto> meetings = meetingService.findAll(companyId);
        return ResponseEntity.ok(meetings);
    }

    /**
     * Retrieves a specific meeting by ID within the context of a company.
     *
     * @param companyId  the ID of the company
     * @param meetingId  the ID of the meeting
     * @return the requested MeetingDto
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
     * Creates a new meeting for a company.
     *
     * @param companyId   the ID of the company
     * @param meetingDto  the meeting data transfer object
     * @return the created MeetingDto with assigned ID
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
     * Updates an existing meeting partially.
     *
     * @param companyId     the ID of the company
     * @param meetingId     the ID of the meeting to update
     * @param newMeetingDto the updated fields of the meeting
     * @return the updated MeetingDto
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
     * Deletes a meeting by ID.
     *
     * @param companyId the ID of the company
     * @param meetingId the ID of the meeting to delete
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