
package ru.avg.server.controller.web.topic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.service.topic.TopicService;

import java.util.List;

/**
 * REST controller for managing topics within a meeting.
 * Endpoints are scoped by {companyId} and {meetingId} to enforce access control and resource hierarchy.
 * Provides standard CRUD operations for topic management.
 */
@RestController
@RequestMapping("/approval/{companyId}/meeting/{meetingId}/topic")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    /**
     * Retrieves all topics associated with a specific meeting.
     *
     * @param companyId the ID of the company (used for routing and future access control)
     * @param meetingId the ID of the meeting for which to retrieve all topics
     * @return ResponseEntity containing a list of TopicDto objects with HTTP status 200 OK
     */
    @Operation(summary = "Get all topics", description = "Retrieves all topics associated with a specific meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of topics")
    })
    @GetMapping
    public ResponseEntity<List<TopicDto>> findAll(
            @PathVariable Integer companyId,
            @PathVariable Integer meetingId) {
        log.debug("Fetching all topics for meetingId: {} in companyId: {}", meetingId, companyId);
        List<TopicDto> topics = topicService.findAllByMeetingId(companyId, meetingId);
        return ResponseEntity.ok(topics);
    }

    /**
     * Retrieves a specific topic by its ID within the context of a meeting and company.
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
     * The input is validated before persistence.
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
            @RequestBody @Valid TopicDto topicDto) {
        log.debug("Creating new topic for meetingId: {} in companyId: {}", meetingId, companyId);
        TopicDto savedTopic = topicService.save(companyId, meetingId, topicDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopic);
    }

    /**
     * Partially updates an existing topic identified by topicId.
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
            @Valid @RequestBody TopicDto topicDto) {
        log.debug("Updating topic with topicId: {} for meetingId: {} in companyId: {}", topicId, meetingId, companyId);
        TopicDto updatedTopic = topicService.edit(companyId, meetingId, topicId, topicDto);
        return ResponseEntity.ok(updatedTopic);
    }

    /**
     * Deletes a topic identified by its ID.
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
}