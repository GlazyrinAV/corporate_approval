package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.utils.validator.inn.Inn;

/**
 * Data Transfer Object for Company entity.
 * Used for creating and updating company records via API.
 *
 * Fields:
 * - id: Optional, assigned by the system on creation
 * - title: Required, business name of the company
 * - inn: Required, 10-digit tax identification number
 * - companyType: Required, must match existing CompanyType
 * - hasBoardOfDirectors: Required, indicates governance structure
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Integer id;

    @NotBlank(message = "Company title must not be blank")
    private String title;

    @Inn
    private Long inn;

    @NotBlank(message = "Company type must not be blank")
    private String companyType;

    @NotNull(message = "Presence of board of directors must be specified")
    private Boolean hasBoardOfDirectors;
}