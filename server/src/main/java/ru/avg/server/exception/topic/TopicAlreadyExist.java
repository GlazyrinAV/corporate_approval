package ru.avg.server.exception.topic;

public class TopicAlreadyExist extends RuntimeException {

    public TopicAlreadyExist() {
        super("Данный вопрос уже существует.");
    }
}