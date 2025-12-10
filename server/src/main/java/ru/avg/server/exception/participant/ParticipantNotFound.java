package ru.avg.server.exception.participant;

public class ParticipantNotFound extends RuntimeException {

    public ParticipantNotFound(String name) {
        super("Участник " + name + " не найден.");
    }

    public ParticipantNotFound(Integer id) {
        super("Участник " + id + " не найден.");
    }
}