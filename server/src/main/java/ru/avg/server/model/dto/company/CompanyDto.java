package ru.avg.server.model.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.utils.validator.inn.Inn;
import ru.avg.server.utils.validator.ogrn.Ogrn;

/**
 * Data Transfer Object representing a company for transfer between application layers, particularly through the API.
 * This class encapsulates the essential attributes of a company and is used when creating or updating company records.
 *
 * <p>Validation is applied to ensure data integrity:
 * <ul>
 *   <li>{@link #title} must not be null or consist only of whitespace.</li>
 *   <li>{@link #inn} must be a valid 10-digit Individual Taxpayer Number (INN).</li>
 *   <li>{@link #registrationNumber} must be a valid 13-digit Main State Registration Number (OGRN).</li>
 *   <li>{@link #companyType} must not be null or blank and should correspond to a recognized company type.</li>
 *   <li>{@link #hasBoardOfDirectors} must be explicitly set to true or false; null values are not allowed.</li>
 * </ul>
 *
 * <p>The class uses Lombok annotations to reduce boilerplate code:
 * <ul>
 *   <li>{@link Data} generates getters, setters, {@code toString()}, {@code equals()}, and {@code hashCode()} methods.</li>
 *   <li>{@link Builder} provides a fluent API for object creation.</li>
 *   <li>{@link NoArgsConstructor} generates a no-argument constructor.</li>
 *   <li>{@link AllArgsConstructor} generates a constructor with all fields.</li>
 * </ul>
 *
 * @see Inn for custom validation logic on the INN field
 * @see Ogrn for custom validation logic on the OGRN field
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    /**
     * Unique identifier of the company, auto-generated upon persistence.
     * This field is optional during creation and will be set by the system.
     * Used to reference an existing company when updating or retrieving data.
     */
    private Integer id;

    /**
     * Official business name of the company.
     * Must not be {@code null} or blank. Used for display, reporting, and identification purposes.
     * Enforced at both API and service layers using {@link NotBlank} constraint.
     */
    @NotBlank(message = "Company title must not be blank")
    private String title;

    /**
     * Individual Taxpayer Number (INN) — a 10-digit tax identification number assigned to legal entities in Russia.
     * Must be a valid INN as validated by the {@link Inn} constraint.
     * This field is required and unique across all companies in the system.
     * The format is strictly numeric and must pass checksum validation defined by Russian tax regulations.
     */
    @Inn
    private Long inn;

    /**
     * Main State Registration Number (OGRN) — a 13-digit identifier assigned to a company upon state registration.
     * Must be a valid OGRN as validated by the {@link Ogrn} constraint.
     * This field is required and unique across all companies.
     * It is used for legal and governmental identification purposes.
     */
    @Ogrn
    private Long registrationNumber;

    /**
     * Type of the company (e.g., LLC, JSC) represented as a string corresponding to the {@link ru.avg.server.model.company.CompanyType} enum.
     * Must not be {@code null} or blank.
     * The value should match one of the predefined types supported by the system.
     * Enforced using {@link NotBlank} with a descriptive error message.
     */
    @NotBlank(message = "Company type must not be blank")
    private String companyType;

    /**
     * Indicates whether the company has a board of directors as part of its governance structure.
     * This is a required field; the value must be explicitly set to {@code true} or {@code false}.
     * Cannot be null — ensures clear corporate governance configuration.
     * Used in business logic to determine meeting types and participant eligibility.
     */
    @NotNull(message = "Presence of board of directors must be specified")
    private Boolean hasBoardOfDirectors;
}