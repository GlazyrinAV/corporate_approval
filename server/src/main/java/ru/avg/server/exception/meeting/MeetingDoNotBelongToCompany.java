package ru.avg.server.exception.meeting;

import lombok.Getter;

/**
 * Exception thrown when attempting to access a meeting in the context of a company that does not own it.
 * This exception is a security mechanism that enforces data isolation and multi-tenancy in the application,
 * preventing cross-company data access. It is typically thrown when a service operation requires both
 * a company context and a meeting reference, but the specified meeting belongs to a different company
 * or has no company association.
 * <p>
 * The exception carries both the company ID and meeting ID to provide complete context for error handling,
 * logging, and debugging purposes. The information can be used to generate meaningful error responses
 * in API endpoints or to implement retry logic with corrected parameters.
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingDoNotBelongToCompany extends RuntimeException {

    /**
     * The identifier of the company whose context was used for the operation.
     * This field stores the company ID that was provided as the expected owner of the meeting,
     * but which does not actually own the meeting being accessed. The company ID helps identify
     * the context in which the security violation occurred.
     *
     */
    private final Integer companyId;

    /**
     * The identifier of the meeting being accessed.
     * This field stores the meeting ID that was attempted to be accessed within the wrong company context.
     * The meeting ID helps identify the specific resource that caused the security violation.
     *
     */
    private final Integer meetingId;

    /**
     * Constructs a new MeetingDoNotBelongToCompany exception with the specified company and meeting identifiers.
     * <p>
     * The constructor creates a descriptive error message that clearly states the security violation:
     * a meeting being accessed does not belong to the specified company. The message follows the format:
     * "Meeting with ID {meetingId} does not belong to Company with ID {companyId}".
     * </p>
     *
     * @param companyId  the ID of the company that was used as context but does not own the meeting; must not be null
     * @param meetingId  the ID of the meeting that was attempted to be accessed; must not be null
     * @throws NullPointerException if either companyId or meetingId is null
     */
    public MeetingDoNotBelongToCompany(Integer companyId, Integer meetingId) {
        super("Meeting with ID %d does not belong to Company with ID %d".formatted(companyId, meetingId));
        this.companyId = companyId;
        this.meetingId = meetingId;
    }
}