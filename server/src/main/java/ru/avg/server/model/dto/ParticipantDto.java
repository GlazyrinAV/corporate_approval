package ru.avg.server.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Participant entity.
 * Used for creating and updating participant records via API.
 *
 * Fields:
 * - id: Optional, assigned by the system on creation
 * - name: Required, full name of the participant
 * - share: Required, ownership percentage (0–100)
 * - companyId: Required, reference to the parent company
 * - type: Required, must match existing ParticipantType
 * - isActive: Required, indicates if the participant is active
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {

    private Integer id;

    @NotBlank(message = "Participant name must not be blank")
    private String name;

    @NotNull(message = "Share must not be null")
    @Min(value = 0, message = "Share must be at least 0")
    @Max(value = 100, message = "Share cannot exceed 100%")
    private Double share;

    @NotNull(message = "Company ID must not be null")
    private Integer companyId;

    @NotBlank(message = "Participant type must not be blank")
    private String type;

    @NotNull(message = "Active status must be specified")
    private Boolean isActive;
}