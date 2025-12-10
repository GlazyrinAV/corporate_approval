package ru.avg.server.exception.meeting;

public class MeetingTypeNotFound extends RuntimeException {
    public MeetingTypeNotFound(String type) {
        super("Meeting type not found: " + type);
    }
}