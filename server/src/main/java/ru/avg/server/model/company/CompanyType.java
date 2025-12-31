package ru.avg.server.model.company;

import lombok.Getter;

/**
 * Enumeration of company types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 *
 * Supported types:
 * - JSC: Joint Stock Company (Акционерное общество)
 * - LLC: Limited Liability Company (Общество с ограниченной ответственностью)
 *
 * This enum is used in {@link ru.avg.server.model.company.Company} to classify organizations.
 */
@Getter
public enum CompanyType {

    JSC("Акционерное общество"),
    LLC("Общество с ограниченной ответственностью");

    private final String title;

    /**
     * Constructs a new CompanyType with the specified display title.
     *
     * @param title the Russian title of the company type
     */
    CompanyType(String title) {
        this.title = title;
    }
}