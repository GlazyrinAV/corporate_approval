package ru.avg.server.exception.company;

public class CompanyTypeNotFound extends RuntimeException {

    public CompanyTypeNotFound(String title) {
        super("CompanyType " + title + " not found.");
    }
}