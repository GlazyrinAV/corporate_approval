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
import ru.avg.server.model.dto.MeetingParticipantCreationDto;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.service.participant.MeetingParticipantService;

import java.util.List;

/**
 * REST controller for managing meeting participants.
 * Endpoints are scoped by {companyId} and {meetingId} for access control.
 * This controller provides operations to retrieve potential and actual participants,
 * add new participants to a meeting, and remove existing ones.
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting/{meetingId}/participants")
@RequiredArgsConstructor
@Slf4j
public class MeetingParticipantController {

    private final MeetingParticipantService meetingParticipantService;

    /**
     * Retrieves a list of potential participants for a specific meeting.
     * Potential participants are users who can be added to the meeting but are not yet participants.
     *
     * @param meetingId the ID of the meeting for which potential participants are retrieved
     * @return ResponseEntity containing a list of MeetingParticipantDto representing potential participants
     */
    @Operation(summary = "Get potential participants", description = "Retrieves a list of potential participants for a specific meeting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of potential participants")
    })
    @GetMapping("/potentials")
    public ResponseEntity<List<MeetingParticipantDto>> findPotentialParticipants(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.debug("Fetching potential participants for meeting: {}", meetingId);
        List<MeetingParticipantDto> potentialParticipants = meetingParticipantService.findPotential(companyId, meetingId);
        return ResponseEntity.ok(potentialParticipants);
    }

    /**
     * Retrieves all current participants of a specific meeting.
     *
     * @param meetingId the ID of the meeting for which participants are retrieved
     * @return ResponseEntity containing a list of MeetingParticipantDto representing current participants
     */
    @Operation(summary = "Get all participants", description = "Retrieves all current participants of a specific meeting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of participants")
    })
    @GetMapping
    public ResponseEntity<List<MeetingParticipantDto>> findParticipants(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.debug("Fetching participants for meetingId: {}", meetingId);
        List<MeetingParticipantDto> participants = meetingParticipantService.findAll(companyId, meetingId);
        return ResponseEntity.ok(participants);
    }

    /**
     * Retrieves a specific participant by their ID within the context of a meeting.
     *
     * @param meetingId     the ID of the meeting to which the participant belongs
     * @param participantId the ID of the participant to retrieve
     * @return ResponseEntity containing the MeetingParticipantDto of the requested participant
     */
    @Operation(summary = "Get participant by ID", description = "Retrieves a specific participant by their ID within the context of a meeting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the participant"),
        @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @GetMapping("/{participantId}")
    public ResponseEntity<MeetingParticipantDto> findParticipant(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer participantId) {
        log.debug("Fetching participant with ID: {} for meeting: {}", participantId, meetingId);
        MeetingParticipantDto participant = meetingParticipantService.findByParticipantId(companyId, meetingId, participantId);
        return ResponseEntity.ok(participant);
    }

    /**
     * Adds new participants to a meeting based on the provided creation data.
     * The input is validated using Jakarta Validation annotations.
     * If validation fails, a ValidationException will be thrown.
     *
     * @param meetingId   the ID of the meeting to which participants will be added
     * @param creationDto the DTO containing a list of potential participants to add
     * @return ResponseEntity containing a list of MeetingParticipantDto representing the added participants
     * with HTTP status 201 Created
     */
    @Operation(summary = "Add participants", description = "Adds new participants to a meeting based on the provided creation data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Participants added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<List<MeetingParticipantDto>> saveMeetingParticipant(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @RequestBody @Valid MeetingParticipantCreationDto creationDto) {
        log.debug("Adding participants to meeting: {} with data: {}", meetingId, creationDto);
        List<MeetingParticipantDto> participants = meetingParticipantService.save(companyId, meetingId, creationDto.getPotentialParticipants());
        return ResponseEntity.status(HttpStatus.CREATED).body(participants);
    }

    /**
     * Removes a participant from a meeting.
     * After successful deletion, returns HTTP 204 No Content.
     *
     * @param meetingId     the ID of the meeting from which the participant will be removed
     * @param participantId the ID of the participant to remove
     * @return ResponseEntity with no content and HTTP status 204 No Content
     */
    @Operation(summary = "Remove participant", description = "Removes a participant from a meeting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Participant removed successfully"),
        @ApiResponse(responseCode = "404", description = "Participant not found")
    })
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeMeetingParticipant(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer participantId) {
        log.debug("Removing participant with ID: {} from meeting: {}", participantId, meetingId);
        meetingParticipantService.delete(companyId, meetingId, participantId);
        return ResponseEntity.noContent().build();
    }
}