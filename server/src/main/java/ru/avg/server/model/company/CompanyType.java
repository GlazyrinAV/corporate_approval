package ru.avg.server.model.company;

import lombok.Getter;

/**
 * Enumeration of company types supported in the system.
 * Each type has a corresponding Russian title for display purposes.
 * <p>
 * Supported types:
 * - JSC: Joint Stock Company (Акционерное общество)
 * - LLC: Limited Liability Company (Общество с ограниченной ответственностью)
 * <p>
 * This enum is used in {@link ru.avg.server.model.company.Company} to classify organizations.
 */
@Getter
public enum CompanyType {

    /**
     * Joint Stock Company (Акционерное общество).
     * A company whose capital is divided into shares and which may conduct open subscription.
     */
    JSC("Акционерное общество"),

    /**
     * Limited Liability Company (Общество с ограниченной ответственностью).
     * A company where members' liability is limited to their contribution to the company's capital.
     */
    LLC("Общество с ограниченной ответственностью");

    /**
     * The Russian-language title of the company type, used for display in the user interface.
     */
    private final String title;

    /**
     * Constructs a new CompanyType with the specified display title.
     *
     * @param title the Russian title of the company type; must not be null
     */
    CompanyType(String title) {
        this.title = title;
    }
}