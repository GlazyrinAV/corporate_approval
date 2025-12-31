package ru.avg.server.controller.web.meeting;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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
    @GetMapping("/potentials")
    public ResponseEntity<List<MeetingParticipantDto>> findPotentialParticipants(
            @PathVariable Integer meetingId) {
        log.debug("Fetching potential participants for meeting: {}", meetingId);
        List<MeetingParticipantDto> potentialParticipants = meetingParticipantService.findPotential(meetingId);
        return ResponseEntity.ok(potentialParticipants);
    }

    /**
     * Retrieves all current participants of a specific meeting.
     *
     * @param meetingId the ID of the meeting for which participants are retrieved
     * @return ResponseEntity containing a list of MeetingParticipantDto representing current participants
     */
    @GetMapping
    public ResponseEntity<List<MeetingParticipantDto>> findParticipants(
            @PathVariable Integer meetingId) {
        log.debug("Fetching participants for meetingId: {}", meetingId);
        List<MeetingParticipantDto> participants = meetingParticipantService.findAll(meetingId);
        return ResponseEntity.ok(participants);
    }

    /**
     * Retrieves a specific participant by their ID within the context of a meeting.
     *
     * @param meetingId     the ID of the meeting to which the participant belongs
     * @param participantId the ID of the participant to retrieve
     * @return ResponseEntity containing the MeetingParticipantDto of the requested participant
     */
    @GetMapping("/{participantId}")
    public ResponseEntity<MeetingParticipantDto> findParticipant(
            @PathVariable Integer meetingId,
            @PathVariable Integer participantId) {
        log.debug("Fetching participant with ID: {} for meeting: {}", participantId, meetingId);
        MeetingParticipantDto participant = meetingParticipantService.findByParticipantId(meetingId, participantId);
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
    @PostMapping
    public ResponseEntity<List<MeetingParticipantDto>> saveMeetingParticipant(
            @PathVariable Integer meetingId,
            @RequestBody @Valid MeetingParticipantCreationDto creationDto) {
        log.debug("Adding participants to meeting: {} with data: {}", meetingId, creationDto);
        List<MeetingParticipantDto> participants = meetingParticipantService.save(creationDto.getPotentialParticipants());
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
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeMeetingParticipant(
            @PathVariable Integer meetingId,
            @PathVariable Integer participantId) {
        log.debug("Removing participant with ID: {} from meeting: {}", participantId, meetingId);
        meetingParticipantService.delete(participantId);
        return ResponseEntity.noContent().build();
    }
}