package ru.avg.server.service.voting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.voting.VotingNotFound;
import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.model.dto.mapper.VotingMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.topic.Topic;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.voting.VotingRepository;
import ru.avg.server.service.voting.VoterService;
import ru.avg.server.service.voting.VotingService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingServiceImpl implements VotingService {

    private final VotingRepository votingRepository;

    private final VotingMapper votingMapper;

    private final VoterService voterService;

    @Override
    public VotingDto create(Topic topic) {
        Voting voting = votingRepository.findByTopicId(topic.getId())
                .orElseThrow(() -> new VotingNotFound(topic.getId()));
        if (voting == null) {
            voting = votingRepository.save(Voting.builder()
                    .topic(topic)
                    .build());
        }
        voterService.create(voting);
        return votingMapper.toDto(voting);
    }

    @Override
    public void deleteByTopicId(Integer topicId) {
        votingRepository.deleteByTopicId(topicId);
    }

    @Override
    public VotingDto findByTopicId(Integer topicId) {
        return votingMapper.toDto(votingRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingNotFound(topicId)));
    }

    @Override
    public VotingDto makeVote(Integer topicId, List<VoterDto> voters) {
        for (VoterDto voter : voters) {
            if (voterService.find(voter.getTopicId()) != null) {
                voterService.makeVote(voter);
            }
        }
        checkVoting(topicId, voters);
        return votingMapper.toDto(votingRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingNotFound(topicId)));
    }

    private void checkVoting(Integer topicId, List<VoterDto> voters) {
        Voting voting = votingRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingNotFound(topicId));
        Double count = 0.0;

        for (VoterDto voter : voters) {
            if (voting.getTopic().getMeeting().getType().equals(MeetingType.BOD)) {
                if (voter.getVote().equals(VoteType.YES.toString())) {
                    count++;
                }
            } else {
                count+=voter.getParticipant().getParticipant().getShare();
            }
        }

        if (voting.getTopic().getMeeting().getType().equals(MeetingType.BOD) &&
                count > voters.size() * 0.5) {
            voting.setAccepted(true);
        } else voting.setAccepted(count > 50);
        votingRepository.save(voting);
    }
}