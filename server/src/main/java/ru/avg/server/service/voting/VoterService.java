package ru.avg.server.service.voting;

import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.voting.Voting;

import java.util.List;

public interface VoterService {

    List<VoterDto> create(Voting voting);

    VoterDto update(VoterDto voterDto);

    void delete(Integer voterId);

    VoterDto find(Integer voterId);

    List<VoterDto> findAllByTopicId(Integer topicId);

    void makeVote(VoterDto voterDto);
}