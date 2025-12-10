package ru.avg.server.service.voting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.voting.VoteTypeNotFound;
import ru.avg.server.exception.voting.VoterNotFound;
import ru.avg.server.model.dto.VoterDto;
import ru.avg.server.model.dto.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.mapper.VoterMapper;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.model.voting.VoteType;
import ru.avg.server.model.voting.Voter;
import ru.avg.server.model.voting.Voting;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.voting.VoterRepository;
import ru.avg.server.service.voting.VoterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {

    private final VoterRepository voterRepository;

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final MeetingParticipantMapper meetingParticipantMapper;

    private final VoterMapper voterMapper;

    @Override
    public List<VoterDto> create(Voting voting) {
        List<VoterDto> result = new ArrayList<>();
        List<MeetingParticipant> currentParticipants = new ArrayList<>();

        if (voting.getVoters() != null) {
            result = voting.getVoters().stream()
                    .map(voterMapper::toDto)
                    .toList();
            result.iterator()
                    .forEachRemaining(x ->
                            currentParticipants.add(meetingParticipantMapper.fromDto(x.getParticipant())));
        }

        List<MeetingParticipant> participants = meetingParticipantRepository.
                findAllByMeetingId(voting.getTopic().getMeeting().getId());
        if (participants != null) {
            for (MeetingParticipant participant : participants) {
                if (!currentParticipants.contains(participant)) {
                    Voter voter = Voter.builder()
                            .voting(voting)
                            .participant(participant)
                            .vote(VoteType.NOT_VOTED)
                            .topic(voting.getTopic())
                            .build();
                    voterRepository.save(voter);
                }
            }
        }
        return result;
    }

    @Override
    public VoterDto update(VoterDto voterDto) {
        Voter voter = voterRepository.findById(voterDto.getId())
                .orElseThrow(() -> new VoterNotFound(voterDto.getId()));
        if (voterDto.isRelatedPartyDeal() != voter.isRelatedPartyDeal()) {
            voter.setRelatedPartyDeal(voterDto.isRelatedPartyDeal());
        }
        return voterMapper.toDto(voterRepository.save(voter));
    }

    @Override
    public void delete(Integer voterId) {
        voterRepository.deleteById(voterId);
    }

    @Override
    public VoterDto find(Integer voterId) {
        return voterMapper.toDto(voterRepository.findById(voterId)
                .orElseThrow(() -> new VoterNotFound(voterId)));
    }

    @Override
    public List<VoterDto> findAllByTopicId(Integer topicId) {
        return voterRepository.findAllByTopic_Id(topicId).stream()
                .map(voterMapper::toDto)
                .toList();
    }

    @Override
    public void makeVote(VoterDto voterDto) {
        Voter voter = voterRepository.findById(voterDto.getId())
                .orElseThrow(() -> new VoterNotFound(voterDto.getId()));
        if (!voterDto.getVote().equals(voter.getVote().getTitle())) {
            voter.setVote(Arrays.stream(VoteType.values())
                    .filter(x -> x.getTitle().equals(voterDto.getVote()))
                    .findFirst()
                    .orElseThrow(() -> new VoteTypeNotFound(voterDto.getVote())));
        }
        voterRepository.save(voter);
    }
}