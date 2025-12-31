package ru.avg.server.model.participant;

import lombok.Getter;

/**
 * Enumeration of participant types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 *
 * Supported types:
 * - OWNER: Owner or founder of the company (Собственник)
 * - MEMBER_OF_BOARD: Member of the Board of Directors (Член совета директоров)
 *
 * This enum is used in {@link Participant} to classify individuals within a company context.
 */
@Getter
public enum ParticipantType {

    OWNER("Собственник"),
    MEMBER_OF_BOARD("Член совета директоров");

    private final String title;

    /**
     * Constructs a new ParticipantType with the specified display title.
     *
     * @param title the Russian title of the participant type
     */
    ParticipantType(String title) {
        this.title = title;
    }
}