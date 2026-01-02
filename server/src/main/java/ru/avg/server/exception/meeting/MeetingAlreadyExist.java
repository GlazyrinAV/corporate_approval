package ru.avg.server.exception.meeting;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Exception thrown when attempting to create a meeting that already exists for a company on a given date.
 * This exception provides structured access to the conflicting company INN and meeting date.
 * <p>
 * This exception is typically thrown by meeting services when a client attempts to create a new meeting
 * for a company on a date when that company already has a meeting scheduled. The exception includes
 * the company's INN (tax identification number) and the conflicting date to help clients understand
 * and resolve the conflict.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * if (meetingRepository.existsByCompanyInnAndDate(company.getInn(), meeting.getDate())) {
 *     throw new MeetingAlreadyExist(company.getInn(), meeting.getDate());
 * }
 * </pre>
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingAlreadyExist extends RuntimeException {

    /**
     * The INN (tax identification number) of the company that already has a meeting on the specified date.
     * This field stores the company identifier that was involved in the conflict, allowing clients
     * to identify which company already has a meeting scheduled on the given date.
     * <p>
     * The INN is a unique business identifier in the Russian corporate system and is used to
     * uniquely identify the company that owns the conflicting meeting.
     * </p>
     *
     */
    private final Integer inn;

    /**
     * The date on which the meeting already exists for the company.
     * This field stores the LocalDate when the conflicting meeting is scheduled, allowing clients
     * to identify the specific date that caused the conflict.
     * <p>
     * The date is used in conjunction with the company INN to uniquely identify the scheduling
     * conflict, as multiple meetings for different companies can exist on the same date.
     * </p>
     *
     */
    private final LocalDate date;

    /**
     * Constructs a new MeetingAlreadyExist exception with the specified company INN and meeting date.
     * <p>
     * The constructor creates a descriptive error message that includes both the company's INN
     * and the date of the conflicting meeting, making it easy to understand the nature of the conflict.
     * The message follows the format: "Meeting already exists for company with INN {inn} on date {date}."
     * </p>
     *
     * @param inn  the INN of the company that already has a meeting on the given date; must not be null
     * @param date the date on which the meeting already exists; must not be null
     * @throws NullPointerException if either inn or date is null
     */
    public MeetingAlreadyExist(Integer inn, LocalDate date) {
        super("Meeting already exists for company with INN %d on date %s.".formatted(inn, date));
        this.inn = inn;
        this.date = date;
    }
}