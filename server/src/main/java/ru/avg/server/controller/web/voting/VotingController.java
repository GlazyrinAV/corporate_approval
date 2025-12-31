package ru.avg.server.controller.web.voting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.VotingCreationDto;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.service.voting.VotingService;

/**
 * REST controller for managing voting operations within a meeting topic.
 * Endpoints are scoped by {companyId}, {meetingId}, and {topicId} to ensure proper access control
 * and resource hierarchy, even though only topicId is currently used by the service.
 */
@RestController
@RequestMapping("/approval/{companyId}/{meetingId}/{topicId}/voting")
@RequiredArgsConstructor
@Slf4j
public class VotingController {

    private final VotingService votingService;

    /**
     * Submits votes for a specific topic identified by topicId.
     * The list of voters and their positions is validated before processing.
     * Returns the created voting record with assigned ID and status 201 Created.
     *
     * @param companyId the ID of the company (used for routing/access control)
     * @param meetingId the ID of the meeting (used for routing/access control)
     * @param topicId   the ID of the topic for which votes are submitted
     * @param voters    the DTO containing the list of participants and their votes
     * @return ResponseEntity with the created VotingDto and HTTP status 201 Created
     */
    @Operation(summary = "Submit votes", description = "Submits votes for a specific topic identified by topicId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Votes submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/make_vote")
    public ResponseEntity<VotingDto> saveVote(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer topicId,
            @Valid @RequestBody VotingCreationDto voters) {
        log.debug("Submitting votes for topicId: {} in meeting: {} of company: {}", topicId, meetingId, companyId);
        VotingDto votingDto = votingService.makeVote(companyId, meetingId, topicId, voters.getVoters());
        return ResponseEntity.status(HttpStatus.CREATED).body(votingDto);
    }
}