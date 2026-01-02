package ru.avg.server.model.meeting;

import lombok.Getter;

/**
 * Enumeration of meeting types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 * <p>
 * Supported types:
 * - BOD: Board of Directors meeting (Совет директоров)
 * - FMP: General Meeting of Participants (Общее собрание участников)
 * - FMS: General Meeting of Shareholders (Общее собрание акционеров)
 * <p>
 * This enum is used in {@link ru.avg.server.model.meeting.Meeting} to classify meeting events.
 */
@Getter
public enum MeetingType {

    /**
     * Board of Directors meeting (Совет директоров).
     * A meeting of the company's board of directors responsible for strategic management and oversight.
     */
    BOD("Совет директоров"),

    /**
     * General Meeting of Participants (Общее собрание участников).
     * A meeting of all participants in a company with limited liability, where key corporate decisions are made.
     */
    FMP("Общее собрание участников"),

    /**
     * General Meeting of Shareholders (Общее собрание акционеров).
     * A meeting of all shareholders of a joint-stock company to make decisions on corporate matters.
     */
    FMS("Общее собрание акционеров");

    /**
     * The Russian-language title of the meeting type, used for display in the user interface.
     */
    private final String title;

    /**
     * Constructs a new MeetingType with the specified display title.
     *
     * @param title the Russian title of the meeting type; must not be null
     */
    MeetingType(String title) {
        this.title = title;
    }
}