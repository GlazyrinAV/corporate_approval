package ru.avg.server.exception.voting;

public class VotingAlreadyExist extends RuntimeException {

    public VotingAlreadyExist() {
        super("Голосование уже создано.");
    }
}