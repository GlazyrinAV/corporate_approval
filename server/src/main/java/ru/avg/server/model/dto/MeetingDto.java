package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for Meeting entity.
 * Used for creating and updating meeting records via API.
 *
 * Fields:
 * - id: Optional, assigned by the system on creation
 * - companyId: Required, reference to the parent company
 * - type: Required, must match existing MeetingType
 * - date: Required, when the meeting takes place
 * - address: Required, physical location of the meeting
 * - secretaryId: Required, ID of the participant acting as secretary
 * - chairmanId: Required, ID of the participant acting as chairman
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto {

    private Integer id;

    @NotNull(message = "Company ID must not be null")
    private Integer companyId;

    @NotBlank(message = "Meeting type must not be blank")
    private String type;

    @NotNull(message = "Meeting date must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotBlank(message = "Meeting address must not be blank")
    private String address;

    private Integer secretaryId;

    private Integer chairmanId;
}