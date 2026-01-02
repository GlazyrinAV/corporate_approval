package ru.avg.server.exception.meeting;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Exception thrown when a meeting cannot be found in the system, either when searching by ID
 * or by company INN and meeting date. This exception extends {@link RuntimeException} and is used
 * throughout the application to indicate that a requested meeting resource does not exist.
 * <p>
 * The exception provides structured access to the search context through getter methods,
 * allowing callers to determine which lookup method was used and what parameters were involved.
 * Depending on the constructor used, either the meeting ID or the company INN and date will be populated,
 * while the other fields will be null.
 * </p>
 * <p>
 * This exception is typically thrown by service and repository layers when:
 * <ul>
 *   <li>A meeting with the specified ID does not exist</li>
 *   <li>No meeting exists for the specified company (identified by INN) on the specified date</li>
 * </ul>
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingNotFound extends RuntimeException {

    /**
     * The identifier of the meeting that was not found.
     * This field is populated when the exception is thrown due to a failed lookup by meeting ID.
     * It will be null when the exception is constructed with company INN and date.
     *
     */
    private final Integer id;

    /**
     * The INN (tax identification number) of the company associated with the meeting that was not found.
     * This field is populated when the exception is thrown due to a failed lookup by company INN and date.
     * It will be null when the exception is constructed with a meeting ID.
     *
     */
    private final Integer inn;

    /**
     * The date of the meeting that was not found.
     * This field is populated when the exception is thrown due to a failed lookup by company INN and date.
     * It will be null when the exception is constructed with a meeting ID.
     *
     */
    private final LocalDate date;

    /**
     * Constructs a new MeetingNotFound exception for a meeting identified by its unique ID.
     * <p>
     * This constructor is used when a meeting lookup by ID fails. The exception message
     * clearly indicates that no meeting with the specified ID exists in the system.
     * The {@link #id} field is populated with the requested ID, while {@link #inn} and
     * {@link #date} are set to null to indicate that the lookup was performed by ID only.
     * </p>
     *
     * @param id the ID of the meeting that was requested but not found in the system; may be null
     * @see #getId()
     * @see #getMessage()
     */
    public MeetingNotFound(Integer id) {
        super("Meeting with ID %d not found.".formatted(id));
        this.id = id;
        this.inn = null;
        this.date = null;
    }

    /**
     * Constructs a new MeetingNotFound exception for a meeting identified by company INN and date.
     * <p>
     * This constructor is used when a meeting lookup by company INN and date fails.
     * The exception message clearly indicates that no meeting exists for the specified
     * company on the specified date. The {@link #inn} and {@link #date} fields are populated
     * with the search parameters, while {@link #id} is set to null to indicate that the
     * lookup was performed by company context and date rather than by meeting ID.
     * </p>
     *
     * @param inn  the INN of the company that was searched; may be null
     * @param date the date on which the meeting was searched; may be null
     * @see #getInn()
     * @see #getDate()
     * @see #getMessage()
     */
    public MeetingNotFound(Integer inn, LocalDate date) {
        super("Meeting for company with INN %d on date %s not found.".formatted(inn, date));
        this.inn = inn;
        this.date = date;
        this.id = null;
    }
}