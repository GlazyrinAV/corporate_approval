package ru.avg.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.avg.server.exception.company.CompanyAlreadyExist;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.company.CompanyTypeNotFound;
import ru.avg.server.exception.meeting.MeetingAlreadyExist;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.*;
import ru.avg.server.exception.topic.TopicAlreadyExist;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoteTypeNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.exception.voting.VotingAlreadyExist;
import ru.avg.server.exception.voting.VotingNotFound;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers in the approval system.
 * This class uses Spring's {@link RestControllerAdvice} to provide centralized exception handling
 * across all {@link org.springframework.web.bind.annotation.RestController} classes.
 * <p>
 * The handler converts domain-specific exceptions into appropriate HTTP responses with meaningful
 * error messages, while also providing proper logging for monitoring and debugging purposes.
 * It handles four main categories of exceptions:
 * <ul>
 *   <li>Entity not found (404 NOT FOUND)</li>
 *   <li>Entity already exists (400 BAD REQUEST)</li>
 *   <li>Validation failures (400 BAD REQUEST with field details)</li>
 *   <li>Unexpected errors (500 INTERNAL SERVER ERROR)</li>
 * </ul>
 * </p>
 *
 * @author AVG
 * @see RestControllerAdvice
 * @see ExceptionHandler
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Handles exceptions related to resources that were not found.
     * This includes various "Not Found" exceptions across different domains (company, meeting, participant, etc.).
     * Returns HTTP 404 status with an error message in the response body.
     * <p>
     * Logs a warning message with the exception details, as these typically represent client-side errors
     * where a requested resource does not exist, rather than system failures.
     * </p>
     *
     * @param exception the exception that was thrown, containing details about the missing resource
     * @return ResponseEntity containing an ErrorResponse with HTTP status 404 (NOT_FOUND)
     * @see CompanyNotFound
     * @see MeetingNotFound
     * @see ParticipantNotFound
     * @see TopicNotFound
     * @see VotingNotFound
     */
    @ExceptionHandler({
            CompanyNotFound.class,
            CompanyTypeNotFound.class,
            MeetingNotFound.class,
            MeetingTypeNotFound.class,
            MeetingParticipantNotFound.class,
            ParticipantNotFound.class,
            ParticipantTypeNotFound.class,
            TopicNotFound.class,
            VoterNotFound.class,
            VoteTypeNotFound.class,
            VotingNotFound.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    /**
     * Handles exceptions related to conflicts when attempting to create duplicate resources.
     * This includes various "Already Exist" exceptions across different domains.
     * Returns HTTP 400 status with an error message in the response body.
     * <p>
     * Logs a warning message with the exception details, as these represent client attempting
     * to create resources that already exist, which is typically a business rule violation
     * rather than a system error.
     * </p>
     *
     * @param exception the exception that was thrown, containing details about the conflicting resource
     * @return ResponseEntity containing an ErrorResponse with HTTP status 400 (BAD_REQUEST)
     * @see CompanyAlreadyExist
     * @see MeetingAlreadyExist
     * @see MeetingParticipantAlreadyExist
     * @see ParticipantAlreadyExist
     * @see TopicAlreadyExist
     * @see VotingAlreadyExist
     */
    @ExceptionHandler({
            CompanyAlreadyExist.class,
            MeetingAlreadyExist.class,
            MeetingParticipantAlreadyExist.class,
            ParticipantAlreadyExist.class,
            TopicAlreadyExist.class,
            VotingAlreadyExist.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception) {
        log.warn("Conflict detected: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exception.getMessage()));
    }

    /**
     * Handles validation exceptions thrown when {@code @Valid} annotation is used on controller method parameters.
     * Extracts field-level validation errors and returns them as a map of field names to error messages.
     * Returns HTTP 400 status with detailed field validation errors in the response body.
     * <p>
     * Logs a warning message with all validation errors, helping to identify client input issues.
     * The response format is a JSON object where keys are field names and values are error messages.
     * </p>
     *
     * @param ex the MethodArgumentNotValidException containing all validation errors
     * @return ResponseEntity containing a map of field names to error messages with HTTP status 400 (BAD_REQUEST)
     * @see MethodArgumentNotValidException
     * @see jakarta.validation.Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles all uncaught exceptions that are not specifically handled by other methods.
     * This acts as a safety net for unexpected errors in the system.
     * Returns HTTP 500 status with a generic error message in the response body.
     * <p>
     * Logs a full error message with stack trace using the error level, as these represent
     * unanticipated system failures that require investigation and fixing.
     * The response contains a generic message to avoid exposing system details to clients.
     * </p>
     *
     * @param exception the unhandled exception that occurred during request processing
     * @return ResponseEntity containing an ErrorResponse with HTTP status 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred. Please try again later."));
    }

    /**
     * Immutable Data Transfer Object (DTO) for error responses.
     * Used to standardize the format of error responses returned by the ErrorHandler.
     * This record contains a single field for the error message, ensuring consistent
     * JSON structure across all error responses.
     * <p>
     * The record automatically generates:
     * <ul>
     *   <li>Constructor with parameter</li>
     *   <li>Getter method (error())</li>
     *   <li>equals(), hashCode(), and toString() methods</li>
     * </ul>
     * </p>
     *
     * @param error the error message to return to the client, never null
     * @author AVG
     * @since 1.0
     */
    public record ErrorResponse(String error) {
    }
}