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
 * Data Transfer Object (DTO) representing the data required to create a new company.
 * This class is used when submitting company creation requests through the API and contains
 * only the essential fields needed for initialization.
 *
 * <p>Validation is applied to ensure data integrity:
 * <ul>
 *   <li>{@link #title} must not be null or consist only of whitespace.</li>
 *   <li>{@link #inn} must be a valid 10-digit Individual Taxpayer Number (INN), validated by the {@link Inn} constraint.</li>
 *   <li>{@link #registrationNumber} must be a valid 13-digit Main State Registration Number (OGRN), validated by the {@link Ogrn} constraint.</li>
 *   <li>{@link #companyType} must not be null or blank and should correspond to a recognized company type.</li>
 *   <li>{@link #hasBoardOfDirectors} must be explicitly set; null values are not allowed.</li>
 * </ul>
 *
 * <p>The class uses Lombok annotations to reduce boilerplate code:
 * <ul>
 *   <li>{@link Data} generates getters, setters, {@code toString()}, {@code equals()}, and {@code hashCode()} methods.</li>
 *   <li>{@link Builder} provides a fluent API for object construction.</li>
 *   <li>{@link AllArgsConstructor} generates a constructor with all fields.</li>
 *   <li>{@link NoArgsConstructor} generates a no-argument constructor required for frameworks like Jackson and JPA.</li>
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
public class NewCompanyDto {

    /**
     * Official business name of the company.
     * Must not be {@code null} or blank. Used for identification and display purposes.
     * Enforced via {@link NotBlank} constraint with a descriptive error message.
     */
    @NotBlank(message = "Company title must not be blank")
    private String title;

    /**
     * Individual Taxpayer Number (INN) — a 10-digit tax identification number unique to the company.
     * Must be a valid INN as defined by Russian tax regulations and verified by the {@link Inn} annotation.
     * The value is checked for correct length and checksum during validation.
     */
    @Inn
    private String inn;

    /**
     * Main State Registration Number (OGRN) — a 13-digit identifier assigned to the company upon state registration.
     * Must be a valid OGRN as validated by the {@link Ogrn} constraint.
     * This field is required and ensures legal traceability of the entity.
     */
    @Ogrn
    private String registrationNumber;

    /**
     * Type of the company (e.g., LLC, JSC) represented as a string.
     * Must correspond to an existing {@code CompanyType} in the system.
     * Cannot be {@code null} or blank.
     * Enforced via {@link NotBlank} constraint with a descriptive error message.
     */
    @NotBlank(message = "Company type must not be blank")
    private String companyType;

    /**
     * Indicates whether the company has a board of directors as part of its corporate governance.
     * This field is required and must be explicitly set to {@code true} or {@code false}.
     * Null values are not permitted, ensuring clear configuration of governance structure.
     * Enforced via {@link NotNull} constraint with a descriptive error message.
     */
    @NotNull(message = "Presence of board of directors must be specified")
    private Boolean hasBoardOfDirectors;
}