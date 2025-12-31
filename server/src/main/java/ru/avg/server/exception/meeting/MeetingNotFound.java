package ru.avg.server.exception.meeting;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Exception thrown when a meeting is not found, either by ID or by company INN and date.
 * Provides structured access to the context (id, inn, date) for logging and error handling.
 */
@Getter
public class MeetingNotFound extends RuntimeException {

    private final Integer id;
    private final Integer inn;
    private final LocalDate date;

    /**
     * Constructs a new MeetingNotFound exception for a meeting identified by ID.
     *
     * @param id the ID of the meeting that was not found
     */
    public MeetingNotFound(Integer id) {
        super("Meeting with ID %d not found.".formatted(id));
        this.id = id;
        this.inn = null;
        this.date = null;
    }

    /**
     * Constructs a new MeetingNotFound exception for a meeting identified by company INN and date.
     *
     * @param inn  the INN of the company
     * @param date the date of the meeting
     */
    public MeetingNotFound(Integer inn, LocalDate date) {
        super("Meeting for company with INN %d on date %s not found.".formatted(inn, date));
        this.inn = inn;
        this.date = date;
        this.id = null;
    }
}