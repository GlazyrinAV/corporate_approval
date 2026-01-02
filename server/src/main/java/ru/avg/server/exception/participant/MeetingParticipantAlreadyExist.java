package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when attempting to add a participant to a meeting who is already associated with that meeting.
 * <p>
 * This exception is used by services and repositories to enforce business rules that prevent duplicate
 * participant entries in a single meeting. It provides access to the participant identifier through the
 * {@link #getId()} method, allowing callers to identify which participant caused the conflict.
 * </p>
 * <p>
 * The exception message follows the format: "MeetingParticipant with id '{id}' already exists."
 * where {id} is replaced with the actual participant ID that was attempted to be added.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.participant.MeetingParticipant
 * @author AVG
 * @since 1.0
 */
@Getter
public class MeetingParticipantAlreadyExist extends RuntimeException {

    /**
     * The identifier of the meeting participant that already exists in the meeting.
     * This field stores the ID of the participant that caused the duplicate conflict,
     * allowing clients of the exception to access the specific participant identifier
     * that triggered the validation failure.
     *
     */
    private final String id;

    /**
     * Constructs a new MeetingParticipantAlreadyExist exception with the specified participant ID.
     * <p>
     * The constructor creates a descriptive error message that includes the participant ID,
     * making it clear which participant already exists in the meeting. The message follows
     * the format: "MeetingParticipant with id '{id}' already exists."
     * </p>
     *
     * @param id the ID of the participant that already exists in the meeting; must not be null
     * @throws NullPointerException if id is null
     * @see #getMessage()
     * @see #getId()
     */
    public MeetingParticipantAlreadyExist(String id) {
        super("MeetingParticipant with id '%s' already exists.".formatted(id));
        this.id = id;
    }
}