package ru.avg.server.controller.web.participant;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
 */
@RestController
@RequestMapping("/approval/{companyId}/participant")
@AllArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * Retrieves all participants for a given company.
     *
     * @param companyId the ID of the company
     * @return ResponseEntity with OK status and list of ParticipantDto
     */
    @GetMapping
    public ResponseEntity<List<ParticipantDto>> findAll(@PathVariable Integer companyId) {
        log.debug("Request to get all participants for companyId={}", companyId);
        List<ParticipantDto> participants = participantService.findAll(companyId);
        return ResponseEntity.ok(participants);
    }

    /**
     * Creates a new participant for a given company.
     *
     * @param companyId       the ID of the company
     * @param participantDto  the participant data transfer object
     * @return ResponseEntity with CREATED status and the saved ParticipantDto
     */
    @PostMapping
    public ResponseEntity<ParticipantDto> save(
            @PathVariable Integer companyId,
            @Valid @RequestBody ParticipantDto participantDto) {
        log.info("Request to create participant for companyId={}: {}", companyId, participantDto);
        ParticipantDto savedParticipant = participantService.save(participantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
    }

    /**
     * Retrieves a specific participant by ID within a company.
     *
     * @param companyId       the ID of the company
     * @param participantId   the ID of the participant
     * @return ResponseEntity with OK status and the ParticipantDto
     */
    @GetMapping("/{participantId}")
    public ResponseEntity<ParticipantDto> findById(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId) {
        log.debug("Request to find participant by id={} for companyId={}", participantId, companyId);
        ParticipantDto participantDto = participantService.findById(participantId);
        return ResponseEntity.ok(participantDto);
    }

    /**
     * Partially updates an existing participant.
     *
     * @param companyId        the ID of the company
     * @param participantId    the ID of the participant to update
     * @param newParticipantDto the updated fields of the participant
     * @return ResponseEntity with OK status and the updated ParticipantDto
     */
    @PatchMapping("/{participantId}")
    public ResponseEntity<ParticipantDto> update(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId,
            @Valid @RequestBody ParticipantDto newParticipantDto) {
        log.info("Request to update participant id={} for companyId={}: {}", participantId, companyId, newParticipantDto);
        ParticipantDto updatedParticipant = participantService.update(participantId, newParticipantDto);
        return ResponseEntity.ok(updatedParticipant);
    }

    /**
     * Deletes a participant by ID.
     *
     * @param companyId      the ID of the company
     * @param participantId  the ID of the participant to delete
     * @return ResponseEntity with NO_CONTENT status
     */
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeById(
            @PathVariable Integer companyId,
            @PathVariable Integer participantId) {
        log.warn("Request to delete participant id={} for companyId={}", participantId, companyId);
        participantService.delete(participantId);
        return ResponseEntity.noContent().build();
    }
}