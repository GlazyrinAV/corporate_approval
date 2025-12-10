package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.participant.MeetingParticipantNotFound;
import ru.avg.server.exception.topic.TopicNotFound;
import ru.avg.server.exception.voting.VoteTypeNotFound;
import ru.avg.server.exception.voting.VotingNotFound;
import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.repository.voting.VotingRepository;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class VoterMapper {

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final MeetingParticipantMapper meetingParticipantMapper;

    private final TopicRepository topicRepository;

    private final VotingRepository votingRepository;

    public Voter fromDto(VoterDto voterDto) {
        return Voter.builder()
                .id(voterDto.getId())
                .isRelatedPartyDeal(voterDto.isRelatedPartyDeal())
                .participant(meetingParticipantRepository.findById(voterDto.getParticipant().getId())
                        .orElseThrow(() -> new MeetingParticipantNotFound(voterDto.getParticipant().getId())))
                .topic(topicRepository.findById(voterDto.getTopicId())
                        .orElseThrow(TopicNotFound::new))
                .voting(votingRepository.findById(voterDto.getVotingId())
                        .orElseThrow(VotingNotFound::new))
                .vote(Arrays.stream(VoteType.values())
                        .filter(x -> x.getTitle().equals(voterDto.getVote()))
                        .findFirst()
                        .orElseThrow(() -> new VoteTypeNotFound(voterDto.getVote())))
                .build();
    }

    public VoterDto toDto(Voter voter) {
        return VoterDto.builder()
                .id(voter.getId())
                .vote(voter.getVote().getTitle())
                .isRelatedPartyDeal(voter.isRelatedPartyDeal())
                .participant(meetingParticipantMapper.toDto(voter.getParticipant()))
                .topicId(voter.getTopic().getId())
                .votingId(voter.getVoting().getId())
                .build();
    }
}