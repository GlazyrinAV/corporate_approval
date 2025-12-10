package ru.avg.server.model.participant;

import lombok.Getter;

@Getter
public enum ParticipantType {

    OWNER ("Собственник"),
    MEMBER_OF_BOARD ("Член совета директоров");

    private final String title;

    ParticipantType(String title) {
        this.title = title;
    }
}