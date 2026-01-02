package ru.avg.server.exception.participant;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a participant that already exists in the system.
 * This exception is used to enforce business rules that prevent duplicate participant entries
 * within the same company or meeting context. It provides access to the participant's name
 * through the {@link #getName()} method, allowing clients to identify which participant
 * caused the conflict and implement appropriate handling logic.
 * <p>
 * The exception message follows the format: "Participant with name '{name}' already exists."
 * where {name} is replaced with the actual participant name that triggered the validation failure.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.participant.Participant
 * @author AVG
 * @since 1.0
 */
@Getter
public class ParticipantAlreadyExist extends RuntimeException {

    /**
     * The name of the participant that already exists in the system.
     * This field stores the participant name that caused the duplicate conflict,
     * allowing clients of the exception to access the specific identifier
     * that triggered the validation failure.
     *
     */
    private final String name;

    /**
     * Constructs a new ParticipantAlreadyExist exception with the specified participant name.
     * <p>
     * The constructor creates a descriptive error message that includes the participant name,
     * making it clear which participant already exists in the system. The message follows
     * the format: "Participant with name '{name}' already exists."
     * </p>
     *
     * @param name the name of the participant that already exists; must not be null
     * @throws NullPointerException if name is null
     * @see #getMessage()
     * @see #getName()
     */
    public ParticipantAlreadyExist(String name) {
        super("Participant with name '%s' already exists.".formatted(name));
        this.name = name;
    }
}