package ru.avg.server.model.meeting;

import lombok.Getter;

/**
 * Enumeration of meeting types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 *
 * Supported types:
 * - BOD: Board of Directors meeting (Совет директоров)
 * - FMP: General Meeting of Participants (Общее собрание участников)
 * - FMS: General Meeting of Shareholders (Общее собрание акционеров)
 *
 * This enum is used in {@link ru.avg.server.model.meeting.Meeting} to classify meeting events.
 */
@Getter
public enum MeetingType {

    BOD("Совет директоров"),
    FMP("Общее собрание участников"),
    FMS("Общее собрание акционеров");

    private final String title;

    /**
     * Constructs a new MeetingType with the specified display title.
     *
     * @param title the Russian title of the meeting type
     */
    MeetingType(String title) {
        this.title = title;
    }
}