package ru.avg.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingDto {

    private Integer id;

    private Integer topicId;

    private boolean isAccepted = false;

    private List<Integer> votersId;
}