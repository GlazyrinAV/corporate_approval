package ru.avg.server.service.voting;

import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.model.topic.Topic;

import java.util.List;

public interface VotingService {

    VotingDto create(Topic topic);

    void deleteByTopicId(Integer topicId);

    VotingDto findByTopicId(Integer topicId);

    VotingDto makeVote(Integer companyId, Integer meetingId, Integer topicId, List<VoterDto> voters);
}