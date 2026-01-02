package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a company that already exists in the system.
 * This exception is typically used during company registration or creation processes when
 * a duplicate is detected based on unique business identifiers such as company title or INN
 * (tax identification number). The exception carries the conflicting company details to
 * facilitate error handling and user feedback.
 * <p>
 * The exception message is localized in Russian as "Компания {title} с ИНН {inn} уже существует.",
 * providing clear information about the existing company that caused the conflict.
 * </p>
 *
 * @see RuntimeException
 * @author AVG
 * @since 1.0
 */
@Getter
public class CompanyAlreadyExist extends RuntimeException {

    /**
     * The name or title of the company that already exists in the system.
     * This field stores the company name that caused the duplicate conflict,
     * allowing clients of the exception to access the specific business name
     * that triggered the validation failure.
     *
     */
    private final String title;

    /**
     * The INN (Individual Taxpayer Number) of the company that already exists.
     * This field stores the tax identification number that caused the duplicate conflict,
     * which serves as a unique business identifier in the Russian corporate system.
     * The INN is used to uniquely identify companies and prevent registration of
     * multiple entities with the same tax identification number.
     *
     */
    private final Integer inn;

    /**
     * Constructs a new CompanyAlreadyExist exception with the specified company details.
     * Initializes the exception with a descriptive error message in Russian that includes
     * both the company title and INN to clearly identify the existing company that
     * prevents the creation of a new one.
     *
     * @param title the name/title of the company that already exists; used in the error message
     *              and stored in the {@link #title} field
     * @param inn   the INN (tax identification number) of the company that already exists;
     *              used in the error message and stored in the {@link #inn} field
     */
    public CompanyAlreadyExist(String title, Integer inn) {
        super("Компания " + title + " с ИНН " + inn + " уже существует.");
        this.title = title;
        this.inn = inn;
    }
}