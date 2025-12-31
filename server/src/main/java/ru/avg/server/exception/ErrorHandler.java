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
 * Global exception handler for REST controllers.
 * Converts domain-specific exceptions into appropriate HTTP responses.
 * Logs warnings for client-side errors (4xx) and errors for unexpected issues.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Handles entity not found exceptions.
     * Returns HTTP 404 with error message.
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
     * Handles entity already exists exceptions.
     * Returns HTTP 400 with error message.
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
     * Handles validation exceptions from @Valid annotations.
     * Returns HTTP 400 with field-level error details.
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
     * Handles all uncaught exceptions.
     * Returns HTTP 500 and logs as error for investigation.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred. Please try again later."));
    }

    /**
     * Immutable error response DTO.
     *
     * @param error the error message to return to the client
     */
    public record ErrorResponse(String error) {}
}