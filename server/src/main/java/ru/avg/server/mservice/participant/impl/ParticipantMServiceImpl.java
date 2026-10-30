package ru.avg.server.mservice.participant.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.mservice.participant.ParticipantMService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.utils.verifier.Verifier;

@Service
@AllArgsConstructor
public class ParticipantMServiceImpl implements ParticipantMService {

    private final Verifier verifier;

    private final MeetingParticipantService meetingParticipantService;

    private final ParticipantService participantService;

    private final ParticipantMapper participantMapper;

    @Override
    public void delete(Integer companyId, Integer participantId) {
        verifier.verifyCompany(companyId);

        boolean hasActiveMeetings = meetingParticipantService.findAllByParticipantId(companyId, participantId)
                .stream()
                .anyMatch(meetingParticipantDto -> {
                            ParticipantDto participant = participantService.findById(companyId, participantId);

                            return Boolean.TRUE.equals(participant.getIsActive());
                        }
                );
        if (!hasActiveMeetings) {
            participantService.delete(companyId, participantId);
        } else {
            Participant participant = participantMapper.fromDto(participantService.findById(companyId, participantId));
            participant.setIsActive(false);
            participantService.update(companyId, participantId, participantMapper.toDto(participant));
        }
    }
}
