package ru.avg.server.exception.company;

public class CompanyAlreadyExist extends RuntimeException {

    public CompanyAlreadyExist(String title, Integer inn) {
        super("Компания " + title + " с ИНН " + inn + " уже существует.");
    }
}