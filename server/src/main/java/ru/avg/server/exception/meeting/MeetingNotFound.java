package ru.avg.server.exception.meeting;

import java.time.LocalDate;

public class MeetingNotFound extends RuntimeException {

    public MeetingNotFound(Integer inn, LocalDate date) {
        super("У компании с ИНН " + inn + " встреча " + date + " не найдена.");
    }

    public MeetingNotFound(Integer id) {
        super("Встреча не найдена" + id);
    }
}