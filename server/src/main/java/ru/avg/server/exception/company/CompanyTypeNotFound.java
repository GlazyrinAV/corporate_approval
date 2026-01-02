package ru.avg.server.exception.company;

import lombok.Getter;

/**
 * Exception thrown when attempting to retrieve or process a company type that does not exist in the system.
 * This exception is typically used when a company type lookup by its title fails, indicating that the
 * requested company type (e.g., "Акционерное общество" or "Общество с ограниченной ответственностью")
 * is not among the supported types defined in {@link ru.avg.server.model.company.CompanyType}.
 * <p>
 * The exception carries the requested company type title to facilitate error handling, logging,
 * and user-friendly error messages that can specify exactly which company type was not found.
 * The error message is formatted in English as "Company type '{title}' not found." where {title}
 * is replaced with the actual company type title that was searched for.
 * </p>
 *
 * @see RuntimeException
 * @see ru.avg.server.model.company.CompanyType
 * @author AVG
 * @since 1.0
 */
@Getter
public class CompanyTypeNotFound extends RuntimeException {

    /**
     * The title of the company type that could not be found in the system.
     * This field stores the requested company type title (in Russian) that was used for lookup
     * but did not match any existing company type in the system. The title is preserved to
     * enable contextual error reporting and debugging.
     *
     */
    private final String title;

    /**
     * Constructs a new CompanyTypeNotFound exception with the specified company type title.
     * Initializes the exception with a descriptive error message in English that includes
     * the company type title, and stores the title for later retrieval through the getter method.
     *
     * @param title the title of the company type that was requested but not found in the system; must not be null
     * @see #getMessage()
     * @see #getTitle()
     */
    public CompanyTypeNotFound(String title) {
        super("Company type '%s' not found.".formatted(title));
        this.title = title;
    }
}