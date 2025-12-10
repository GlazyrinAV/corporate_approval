package ru.avg.server.exception.meeting;

import java.time.LocalDate;

public class MeetingAlreadyExist extends RuntimeException {

    public MeetingAlreadyExist(Integer inn, LocalDate date) {
        super("У компании с ИНН " + inn + "уже имеется встреча " + date);
    }
}