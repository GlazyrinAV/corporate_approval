package ru.avg.server.exception.participant;

public class ParticipantAlreadyExist extends RuntimeException {

    public ParticipantAlreadyExist(String name) {
        super("Участник " + name + " уже существует.");
    }
}