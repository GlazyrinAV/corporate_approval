package ru.avg.server.exception.participant;

import lombok.Getter;

@Getter
public class MeetingParticipantEqual extends RuntimeException {

    private final Integer id;

    public MeetingParticipantEqual(Integer id) {
        super("MeetingParticipant with id '%s' already used.".formatted(id));
        this.id = id;
    }

}
