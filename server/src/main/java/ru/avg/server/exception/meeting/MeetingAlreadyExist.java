package ru.avg.server.exception.meeting;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Exception thrown when attempting to create a meeting that already exists for a company on a given date.
 * This exception provides structured access to the conflicting company INN and meeting date.
 */
@Getter
public class MeetingAlreadyExist extends RuntimeException {

    private final Integer inn;
    private final LocalDate date;

    /**
     * Constructs a new MeetingAlreadyExist exception with the specified company INN and meeting date.
     *
     * @param inn  the INN of the company that already has a meeting on the given date
     * @param date the date on which the meeting already exists
     */
    public MeetingAlreadyExist(Integer inn, LocalDate date) {
        super("Meeting already exists for company with INN %d on date %s.".formatted(inn, date));
        this.inn = inn;
        this.date = date;
    }
}