package ru.avg.server.exception.company;

public class CompanyNotFound extends RuntimeException {

    public CompanyNotFound(Integer id) {
        super("Компания с ID " + id + " не найдена.");
    }
}
