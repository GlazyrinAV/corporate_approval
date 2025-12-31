package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.repository.voting.VoterRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class VotingMapper {

    private TopicRepository topicRepository;

    private VoterRepository voterRepository;

    public Voting fromDto(VotingDto votingDto) {
        List<Voter> voters = new ArrayList<>();
        for (Integer id : votingDto.getVotersId()) {
            voters.add(voterRepository.findById(id)
                    .orElseThrow(() -> new VoterNotFound(id)));
        }
        return Voting.builder()
                .id(votingDto.getId())
                .isAccepted(votingDto.isAccepted())
                .topic(topicRepository.findById(votingDto.getTopicId())
                        .orElseThrow(() -> new TopicNotFound(votingDto.getTopicId())))
                .voters(voters)
                .build();
    }

    public VotingDto toDto(Voting voting) {
        List<Integer> voters = new ArrayList<>();
        List<Voter> voterList = voterRepository.findAllByTopic_Id(voting.getTopic().getId());
        if (voterList != null) {
            voterList.iterator()
                    .forEachRemaining(voter -> voters.add(voter.getId()));
        }
        return VotingDto.builder()
                .id(voting.getId())
                .isAccepted(voting.isAccepted())
                .topicId(voting.getTopic().getId())
                .votersId(voters)
                .build();
    }
}