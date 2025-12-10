package ru.avg.server.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {

    private Integer id;

    @NotBlank(message = "Тема должна быть указана")
    private String title;

    private Integer meetingId;
}