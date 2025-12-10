package ru.avg.server.exception.voting;

public class VoteTypeNotFound extends RuntimeException {
    public VoteTypeNotFound(String type) {
        super("Vote type not found: " + type);
    }
}