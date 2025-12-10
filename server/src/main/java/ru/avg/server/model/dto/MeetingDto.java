package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto {

    private Integer id;

    private Integer companyId;

    @NotBlank(message = "Вид встречи должен быть указан")
    private String type;

    @NotNull(message = "Дата собрания должна быть указана")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date = LocalDate.now();

    @NotBlank(message = "Адрес встречи должен быть указан")
    private String address;

    private Integer secretaryId;

    private Integer chairmanId;
}