package ru.avg.server.service.participant.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.participant.ParticipantAlreadyExist;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.dto.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.mapper.ParticipantMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.participant.ParticipantRepository;
import ru.avg.server.service.participant.ParticipantService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

    private final MeetingParticipantRepository meetingParticipantRepository;

    private final ParticipantMapper participantMapper;

    private final MeetingParticipantMapper meetingParticipantMapper;


    @Override
    public ParticipantDto save(ParticipantDto participantDto) {
        return participantMapper.toDto(participantRepository.save(participantMapper.fromDto(participantDto)));
    }

    @Override
    @Transactional
    public ParticipantDto update(Integer participantId, ParticipantDto newParticipantDto) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFound(participantId));
        Participant newParticipant = participantMapper.fromDto(newParticipantDto);

        if (!newParticipant.getName().isBlank()) {
            participant.setName(newParticipant.getName());
        }

        if (newParticipant.getShare() != null) {
            participant.setShare(newParticipant.getShare());
        }

        if (!newParticipant.getType().equals(participant.getType())) {
            if (participantRepository.findAllByCompanyId(participant.getCompany().getId()).stream()
                    .noneMatch(p -> p.getName().equals(participant.getName()) &&
                            p.getShare().equals(participant.getShare()) &&
                            p.getCompany().getInn().equals(participant.getCompany().getInn()) &&
                            p.getType().equals(newParticipant.getType()))) {
                participant.setType(newParticipant.getType());
            } else {
                throw new ParticipantAlreadyExist(participant.getName());
            }
        }

        if (newParticipant.getIsACtive() != null) {
            participant.setIsACtive(newParticipant.getIsACtive());
        }

        return participantMapper.toDto(participantRepository.save(participant));
    }

    @Override
    public void delete(Integer participantId) {
        List<MeetingParticipantDto> activeMeetings = meetingParticipantRepository.findByParticipantId(participantId).stream()
                .map(meetingParticipantMapper::toDto)
                .toList();
        if (activeMeetings.isEmpty()) {
            participantRepository.deleteById(participantId);
        } else {
            Participant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new ParticipantNotFound(participantId));
            participant.setIsACtive(false);
        }
    }

    @Override
    public ParticipantDto find(String name, Integer companyId, ParticipantType type) {
        return participantMapper.toDto(participantRepository.findByNameAndCompanyIdAndType(name, companyId, type));
    }

    @Override
    public List<ParticipantDto> findAllByMeetingType(Integer companyId, MeetingType type) {
        List<Participant> participants = participantRepository.findAllByCompanyId(companyId);
        List<ParticipantDto> result = new ArrayList<>();
        if (participants != null) {
            for (Participant participant : participants) {
                if ((!type.equals(MeetingType.BOD) && participant.getType().equals(ParticipantType.MEMBER_OF_BOARD)) ||
                        (type.equals(MeetingType.BOD) && participant.getType().equals(ParticipantType.OWNER))
                ) {
                    continue;
                }
                result.add(participantMapper.toDto(participant));
            }
        }
        return result;
    }

    @Override
    public List<ParticipantDto> findAll(Integer companyId) {
        List<Participant> participants = participantRepository.findAllByCompanyId(companyId);
        return participants.stream()
                .map(participantMapper::toDto)
                .toList();
    }

    @Override
    public ParticipantDto findById(Integer participantId) {
        return participantMapper.toDto(participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipantNotFound(participantId)));
    }
}