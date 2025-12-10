package ru.avg.server.exception.participant;

public class ParticipantTypeNotFound extends RuntimeException
{

    public ParticipantTypeNotFound(String type) {
        super("Participant type not found: " + type);
    }
}