package ru.avg.server.model.company;

import lombok.Getter;

@Getter
public enum CompanyType {

    JSC ("Акционерное общество"),
    LLC ("Общество с ограниченной ответственностью");

    private final String title;

    CompanyType(String title) {
        this.title = title;
    }
}