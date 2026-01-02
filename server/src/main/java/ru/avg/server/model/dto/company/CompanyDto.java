package ru.avg.server.model.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.utils.validator.inn.Inn;

/**
 * Data Transfer Object representing a company for transfer between application layers, particularly through the API.
 * This class encapsulates the essential attributes of a company and is used when creating or updating company records.
 *
 * <p>Validation is applied to ensure data integrity:
 * <ul>
 *   <li>{@link #title} must not be null or consist only of whitespace.</li>
 *   <li>{@link #inn} must be a valid 10-digit Individual Taxpayer Number (INN).</li>
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
     */
    private Integer id;

    /**
     * Official business name of the company.
     * Must not be {@code null} or blank. Used for display and identification purposes.
     */
    @NotBlank(message = "Company title must not be blank")
    private String title;

    /**
     * Individual Taxpayer Number (INN) — a 10-digit tax identification number.
     * Must be a valid INN as validated by the {@link Inn} constraint.
     */
    @Inn
    private Long inn;

    /**
     * Type of the company (e.g., LLC, JSC) represented as a string.
     * Must correspond to an existing {@code CompanyType} entry in the system.
     * Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Company type must not be blank")
    private String companyType;

    /**
     * Indicates whether the company has a board of directors as part of its governance structure.
     * This is a required field; the value must be explicitly set to {@code true} or {@code false}.
     */
    @NotNull(message = "Presence of board of directors must be specified")
    private Boolean hasBoardOfDirectors;
}