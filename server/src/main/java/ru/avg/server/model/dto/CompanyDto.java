package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.utils.validator.inn.Inn;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Integer id;

    @NotBlank(message = "Наименование не должно быть пустым")
    private String title;

    @Inn(message = "ИНН должен состоять из 10 цифр")
    private Long inn;

    @NotBlank(message = "Тип компании должен быть заполнен")
    private String companyType;

    @NotNull(message = "Необходимо указать наличие совета директоров")
    private Boolean hasBoardOfDirectors = false;
}