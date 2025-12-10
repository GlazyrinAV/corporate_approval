package ru.avg.server.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {

    private Integer id;

    @NotBlank(message = "ФИО участника должно быть указано")
    private String name;

    @Min(value = 0, message = "значение должно быть больше 0")
    @Max(value = 100, message = "значение не может превышать 100%")
    private Double share = 0.0;

    @Positive
    private Integer companyId;

    @NotBlank(message = "Вид участника должен быть указан")
    private String type;

    private Boolean isActive;
}