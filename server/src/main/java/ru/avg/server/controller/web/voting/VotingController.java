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
import ru.avg.server.model.dto.voting.NewVotingDto;
import ru.avg.server.model.dto.voting.VotingDto;
import ru.avg.server.service.voting.VotingService;

/**
 * REST controller for managing voting operations within a meeting topic.
 * Endpoints are scoped by {companyId}, {meetingId}, and {topicId} to ensure proper access control
 * and resource hierarchy, even though only topicId is currently used by the service.
 * <p>
 * This controller provides endpoints for submitting votes on meeting topics, handling the creation
 * of voting records that capture participant positions. The three-level path structure ({companyId}/{meetingId}/{topicId})
 * enforces proper resource scoping and access control, ensuring votes can only be submitted within
 * the correct organizational and meeting context.
 * </p>
 *
 * @author AVG
 * @see VotingService
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/{companyId}/{meetingId}/{topicId}/voting")
@RequiredArgsConstructor
@Slf4j
public class VotingController {

    /**
     * Service responsible for handling business logic related to voting operations.
     * This dependency is injected by Spring using constructor injection (enabled by {@link RequiredArgsConstructor})
     * and provides the functionality for processing vote submissions, validating voter eligibility,
     * calculating voting results, and persisting voting records.
     * <p>
     * The service encapsulates data access, business rules, and validation logic, allowing this controller
     * to focus solely on request handling, parameter binding, and response construction.
     * </p>
     *
     * @see VotingService
     */
    private final VotingService votingService;

    /**
     * Submits votes for a specific topic identified by topicId.
     * This endpoint processes a voting request where participants express their positions on a meeting topic.
     * The input data is validated using Jakarta Validation annotations before processing.
     * <p>
     * The voting process involves:
     * <ul>
     *   <li>Validating that voters are eligible participants of the meeting</li>
     *   <li>Ensuring no duplicate votes from the same participant</li>
     *   <li>Calculating voting results (for, against, abstain, etc.)</li>
     *   <li>Persisting the voting record with timestamps and status</li>
     * </ul>
     * </p>
     *
     * @param companyId the ID of the company (used for routing and access control)
     * @param meetingId the ID of the meeting (used for routing and access control)
     * @param topicId   the ID of the topic for which votes are being submitted
     * @param voters    the DTO containing the list of participants and their voting positions,
     *                  must not be null and must pass validation constraints
     * @return ResponseEntity containing the created VotingDto with calculated results and metadata,
     * with HTTP status 201 Created
     * @throws jakarta.validation.ConstraintViolationException if the input data fails validation
     * @see VotingService#makeVote(Integer, Integer, Integer, java.util.List)
     * @see NewVotingDto
     * @see VotingDto
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
            @Valid @RequestBody NewVotingDto voters) {
        log.debug("Submitting votes for topicId: {} in meeting: {} of company: {}", topicId, meetingId, companyId);
        VotingDto votingDto = votingService.makeVote(companyId, meetingId, topicId, voters.getVoters());
        return ResponseEntity.status(HttpStatus.CREATED).body(votingDto);
    }
}