package ru.avg.server.exception.participant;

public class MeetingParticipantNotFound extends RuntimeException {
    public MeetingParticipantNotFound(Integer id) {
        super("Could not find meeting participant with id " + id);
    }
}
