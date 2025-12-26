package ru.avg.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.avg.server.exception.company.CompanyAlreadyExist;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.company.CompanyTypeNotFound;
import ru.avg.server.exception.meeting.MeetingAlreadyExist;
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

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({CompanyNotFound.class, CompanyTypeNotFound.class, MeetingTypeNotFound.class,
            MeetingTypeNotFound.class, MeetingParticipantNotFound.class, ParticipantNotFound.class,
            ParticipantTypeNotFound.class, TopicNotFound.class, VoterNotFound.class, VoteTypeNotFound.class,
            VotingNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFound(RuntimeException exception) {
        return sendErrorResponse(exception.getMessage());
    }

    @ExceptionHandler({CompanyAlreadyExist.class, MeetingAlreadyExist.class, MeetingParticipantAlreadyExist.class,
            ParticipantAlreadyExist.class, TopicAlreadyExist.class, VotingAlreadyExist.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse entityAlreadyExist(RuntimeException exception) {
        return sendErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation failed: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse sendErrorResponse(String description) {
        log.warn(description);
        return new ErrorResponse(description);
    }

    public record ErrorResponse(String error) {
    }
}