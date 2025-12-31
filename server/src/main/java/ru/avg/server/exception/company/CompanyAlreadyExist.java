package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a company that already exists.
 * Typically used during company registration when a duplicate INN or title is detected.
 */
@Getter
public class CompanyAlreadyExist extends RuntimeException {

    private final String title;
    private final Integer inn;

    /**
     * Constructs a new CompanyAlreadyExist exception with the specified company details.
     *
     * @param title the name/title of the company that already exists
     * @param inn   the INN (tax identification number) of the company that already exists
     */
    public CompanyAlreadyExist(String title, Integer inn) {
        super("Компания " + title + " с ИНН " + inn + " уже существует.");
        this.title = title;
        this.inn = inn;
    }
}