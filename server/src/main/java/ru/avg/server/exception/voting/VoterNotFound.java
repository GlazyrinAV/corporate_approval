package ru.avg.server.exception.voting;

public class VoterNotFound extends RuntimeException {

    public VoterNotFound(String name) {
        super("Голосующий " + name + " не найден.");
    }

    public VoterNotFound(Integer id) {
        super("Голосующий с ID " + id + " не найден.");
    }
}