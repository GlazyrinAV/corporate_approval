package ru.avg.server.model.voting;

import lombok.Getter;

@Getter
public enum VoteType {

    NOT_VOTED ("НЕ ГОЛОСОВАЛ"),
    YES ("ЗА"),
    NO ("ПРОТИВ"),
    ABSTAINED ("ВОЗДЕРЖАЛСЯ");

    private final String title;

    VoteType(String title) {
        this.title = title;
    }
}