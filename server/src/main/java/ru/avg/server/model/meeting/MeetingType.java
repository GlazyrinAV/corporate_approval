package ru.avg.server.model.meeting;

import lombok.Getter;

@Getter
public enum MeetingType {

    BOD ("Совет директоров"),
    FMP ("Общее собрание участников"),
    FMS ("Общее собрание акционеров");

    private final String title;

    MeetingType(String title) {
        this.title = title;
    }

}