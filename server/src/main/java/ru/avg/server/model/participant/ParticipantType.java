package ru.avg.server.model.participant;

import lombok.Getter;

/**
 * Enumeration of participant types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 *
 * <p>Supported types:
 * <ul>
 *   <li>{@link #OWNER} — Owner or founder of the company (Собственник)</li>
 *   <li>{@link #MEMBER_OF_BOARD} — Member of the Board of Directors (Член совета директоров)</li>
 * </ul>
 * </p>
 *
 * <p>This enum is used in {@link Participant} to classify individuals within a company context
 * and determine their role in corporate governance and meetings.</p>
 *
 * @see Participant
 * @author AVG
 * @since 1.0
 */
@Getter
public enum ParticipantType {

    /**
     * Represents an owner or founder of the company.
     * Displayed as "Собственник" in the user interface.
     */
    OWNER("Собственник"),

    /**
     * Represents a member of the Board of Directors.
     * Displayed as "Член совета директоров" in the user interface.
     */
    MEMBER_OF_BOARD("Член совета директоров");

    /**
     * The Russian-language title of the participant type, used for display in the user interface.
     */
    private final String title;

    /**
     * Constructs a new ParticipantType with the specified display title.
     *
     * @param title the Russian title of the participant type; must not be {@code null}
     * @throws NullPointerException if {@code title} is {@code null}
     */
    ParticipantType(String title) {
        this.title = title;
    }
}