package ru.avg.server.model.dto.participant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the data required to create a new participant.
 * This class is used when submitting participant creation requests through the API and contains
 * only the essential fields needed for initialization.
 *
 * <p>Validation is applied to ensure data integrity:
 * <ul>
 *   <li>{@link #name} must not be {@code null} or blank.</li>
 *   <li>{@link #share} must be between 0 and 100 (inclusive) and cannot be {@code null}.</li>
 *   <li>{@link #companyId} must not be {@code null}, ensuring every participant is associated with a company.</li>
 *   <li>{@link #type} must correspond to an existing {@link ru.avg.server.model.participant.ParticipantType} and cannot be blank.</li>
 *   <li>{@link #isActive} must be explicitly set to {@code true} or {@code false}; null values are not allowed.</li>
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
 * @author AVG
 * @see ParticipantDto for the full participant representation including ID
 * @see ru.avg.server.model.participant.ParticipantType for valid type values
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewParticipantDto {

    /**
     * Full name of the participant.
     * Must not be {@code null} or blank. Used for identification and display purposes.
     */
    @NotBlank(message = "Participant name must not be blank")
    private String name;

    /**
     * Ownership share or percentage held by the participant in the company.
     * Must be between 0 and 100 (inclusive). Cannot be {@code null}.
     */
    @NotNull(message = "Share must not be null")
    @Min(value = 0, message = "Share must be at least 0")
    @Max(value = 100, message = "Share cannot exceed 100%")
    private Double share;

    /**
     * Identifier of the company to which this participant belongs.
     * Must not be {@code null} — every participant must be associated with a company.
     */
    @NotNull(message = "Company ID must not be null")
    private Integer companyId;

    /**
     * Type or classification of the participant (e.g., OWNER, MEMBER_OF_BOARD).
     * Must correspond to an existing {@link ru.avg.server.model.participant.ParticipantType} entry in the system.
     * Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Participant type must not be blank")
    private String type;

    /**
     * Indicates whether the participant is currently active in the company’s operations.
     * Used for lifecycle management; must be explicitly set to {@code true} or {@code false}.
     */
    @NotNull(message = "Active status must be specified")
    private Boolean isActive;
}