package ru.avg.server.controller.web.topic;

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
import ru.avg.server.model.dto.topic.NewTopicDto;
import ru.avg.server.model.dto.topic.TopicDto;
import ru.avg.server.service.topic.TopicService;

/**
 * REST controller for managing topics within a meeting.
 * Endpoints are scoped by {companyId} and {meetingId} to enforce access control and resource hierarchy.
 * Provides standard CRUD operations for topic management.
 * <p>
 * This controller handles HTTP requests related to topic operations and delegates business logic
 * to the {@link TopicService}. It uses Spring's {@link RestController} annotation to indicate
 * that this class handles RESTful web services. The {@link RequestMapping} annotation defines
 * the base path for all endpoints in this controller, ensuring all operations are scoped to
 * a specific company and meeting.
 * </p>
 * <p>
 * The controller includes comprehensive logging via {@link Slf4j} annotation to aid in monitoring
 * and debugging. Different log levels are used appropriately: INFO for mutation operations
 * (POST, PATCH, DELETE) and DEBUG for query operations (GET). This helps in tracking system
 * activity without overwhelming the logs with read operations.
 * </p>
 *
 * @author AVG
 * @see TopicService
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting/{meetingId}/topic")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    /**
     * Service responsible for handling business logic related to topic management.
     * This dependency is injected by Spring using constructor injection (enabled by {@link RequiredArgsConstructor})
     * and provides all necessary operations for creating, reading, updating, and deleting topics.
     * <p>
     * The service encapsulates data access, validation, and business rules, allowing this controller
     * to focus solely on request handling, parameter binding, and response construction.
     * </p>
     *
     * @see TopicService
     */
    private final TopicService topicService;

    /**
     * Retrieves all topics associated with a specific meeting.
     * This endpoint returns a list of all topics for the given meeting within the specified company.
     *
     * @param companyId the ID of the company (used for routing and future access control)
     * @param meetingId the ID of the meeting for which to retrieve all topics
     * @param page      the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit     the maximum number of elements to return per page;
     *                  must be between 1 and 20 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link TopicDto} objects representing
     * the topics for the requested page, including full pagination metadata (total elements, total pages, etc.),
     * with HTTP status 200 (OK)
     * @see TopicService#findAllByMeetingId(Integer, Integer, Integer, Integer)
     * @see TopicDto
     * @see Page
     */
    @Operation(summary = "Get all topics", description = "Retrieves all topics associated with a specific meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of topics")
    })
    @GetMapping
    public ResponseEntity<Page<TopicDto>> findAll(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer limit) {
        log.debug("Fetching all topics for meetingId={} in companyId={} page={} limit={}",
                meetingId, companyId, page, limit);
        Page<TopicDto> topics = topicService.findAllByMeetingId(companyId, meetingId, page, limit);
        return ResponseEntity.ok(topics);
    }

    /**
     * Retrieves a specific topic by its ID within the context of a meeting and company.
     * The company and meeting IDs are used for scoping and security purposes to ensure
     * the requested topic belongs to the specified meeting and company.
     *
     * @param companyId the ID of the company (for scoping and security)
     * @param meetingId the ID of the meeting to which the topic belongs
     * @param topicId   the ID of the topic to retrieve
     * @return ResponseEntity containing the requested TopicDto with HTTP status 200 OK
     */
    @Operation(summary = "Get topic by ID", description = "Retrieves a specific topic by its ID within the context of a meeting and company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the topic"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDto> findById(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer topicId) {
        log.debug("Fetching topic with topicId: {} for meetingId: {} in companyId: {}", topicId, meetingId, companyId);
        TopicDto topic = topicService.findById(companyId, meetingId, topicId);
        return ResponseEntity.ok(topic);
    }

    /**
     * Creates a new topic for a specific meeting.
     * The input data is validated using Jakarta Validation annotations before being persisted.
     * If validation fails, a MethodArgumentNotValidException will be thrown.
     * <p>
     * This operation creates a new topic within the specified meeting context, establishing
     * the relationship between the topic and its parent meeting. The service layer handles
     * the business logic for ensuring the meeting exists and the operation is valid.
     * </p>
     *
     * @param companyId the ID of the company (for scoping)
     * @param meetingId the ID of the meeting for which the topic is created
     * @param topicDto  the TopicDto containing the data for the new topic
     * @return ResponseEntity containing the saved TopicDto with HTTP status 201 Created
     */
    @Operation(summary = "Create new topic", description = "Creates a new topic for a specific meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Topic created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<TopicDto> save(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @RequestBody @Valid NewTopicDto topicDto) {
        log.debug("Creating new topic for meetingId: {} in companyId: {}", meetingId, companyId);
        TopicDto savedTopic = topicService.save(companyId, meetingId, topicDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopic);
    }

    /**
     * Partially updates an existing topic identified by topicId.
     * Only the fields provided in the request body will be updated, leaving others unchanged.
     * The input is validated before processing.
     * <p>
     * This operation performs a partial update (PATCH semantics) on the specified topic,
     * allowing clients to update only specific fields without sending the complete resource.
     * The service layer handles merging the provided fields with the existing entity.
     * </p>
     *
     * @param companyId the ID of the company (for scoping)
     * @param meetingId the ID of the meeting to which the topic belongs
     * @param topicId   the ID of the topic to update
     * @param topicDto  the TopicDto containing fields to update
     * @return ResponseEntity containing the updated TopicDto with HTTP status 200 OK
     */
    @Operation(summary = "Update topic", description = "Partially updates an existing topic identified by topicId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic updated successfully"),
            @ApiResponse(responseCode = "404", description = "Topic not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PatchMapping("/{topicId}")
    public ResponseEntity<TopicDto> update(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer topicId,
            @Valid @RequestBody NewTopicDto topicDto) {
        log.debug("Updating topic with topicId: {} for meetingId: {} in companyId: {}", topicId, meetingId, companyId);
        TopicDto updatedTopic = topicService.update(companyId, meetingId, topicId, topicDto);
        return ResponseEntity.ok(updatedTopic);
    }

    /**
     * Deletes a topic identified by its ID.
     * This operation removes the topic from the specified meeting and deletes it from persistence.
     * After successful deletion, returns HTTP 204 No Content with no response body.
     * <p>
     * The company and meeting IDs are used for scoping and access control to ensure
     * the client has permission to delete the specified topic. The service layer handles
     * the actual deletion operation and any necessary cleanup.
     * </p>
     *
     * @param companyId the ID of the company (for scoping)
     * @param meetingId the ID of the meeting from which the topic will be removed
     * @param topicId   the ID of the topic to delete
     * @return ResponseEntity with no content and HTTP status 204 No Content
     */
    @Operation(summary = "Delete topic", description = "Deletes a topic identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Topic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId,
            @PathVariable Integer topicId) {
        log.warn("Deleting topic with topicId: {} from meetingId: {} in companyId: {}", topicId, meetingId, companyId);
        topicService.delete(companyId, meetingId, topicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for topics by criteria with pagination support.
     * <p>
     * This endpoint performs a case-insensitive partial match search on topic titles
     * and descriptions within the specified meeting context. The results are returned
     * in a paginated format to support efficient handling of large datasets.
     * </p>
     * <p>
     * If the search criteria is empty or not provided, the method returns all topics
     * for the meeting (subject to pagination). The maximum length of the search string
     * is limited to 100 characters to prevent abuse and ensure system stability.
     * </p>
     *
     * @param companyId the ID of the company (for scoping and access control)
     * @param meetingId the ID of the meeting to which topics belong
     * @param criteria  the search string to match against topic titles and descriptions;
     *                  optional, defaults to empty string if not provided;
     *                  maximum length is 100 characters
     * @param page      the zero-based page number to retrieve; must be non-negative (default: 0)
     * @param limit     the maximum number of elements to return per page;
     *                  must be between 1 and 20 (inclusive, default: 10)
     * @return a ResponseEntity containing a {@link Page} of {@link TopicDto} objects representing
     * the topics matching the search criteria for the requested page,
     * including full pagination metadata (total elements, total pages, etc.),
     * with HTTP status 200 (OK)
     * @see TopicService#findByCriteria(Integer, Integer, String, Integer, Integer)
     * @see TopicDto
     * @see Page
     */
    @Operation(summary = "Search topics by criteria with pagination",
            description = "Finds topics by partial match on title or description with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page of matching topics"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters (page < 0 or limit not in 1-20 range) or criteria exceeds 100 characters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<TopicDto>> findByCriteria(@PathVariable Integer companyId,
                                                         @PathVariable Integer meetingId,
                                                         @RequestParam(required = false, defaultValue = "") @Size(max = 100) String criteria,
                                                         @RequestParam(defaultValue = "0") @Min(0) Integer page,
                                                         @RequestParam(defaultValue = "10") @Min(1) @Max(20) Integer limit) {
        log.debug("Finding topics by criteria with pagination(page: {}, limit: {}): {}", page, limit, criteria);
        Page<TopicDto> topics = topicService.findByCriteria(companyId, meetingId, criteria, page, limit);
        return ResponseEntity.ok(topics);
    }
}