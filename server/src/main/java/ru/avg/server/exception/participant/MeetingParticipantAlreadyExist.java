package ru.avg.server.exception.participant;

public class MeetingParticipantAlreadyExist extends RuntimeException {
    public MeetingParticipantAlreadyExist(String id) {
        super("MeetingParticipant with id " + id + " already exists");
    }
}