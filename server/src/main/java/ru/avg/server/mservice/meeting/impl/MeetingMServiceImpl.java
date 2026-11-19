package ru.avg.server.mservice.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.participant.MeetingParticipantEqual;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.meeting.MeetingUpdateDto;
import ru.avg.server.model.dto.meeting.NewMeetingDto;
import ru.avg.server.model.dto.meeting.mapper.MeetingMapper;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;
import ru.avg.server.model.dto.participant.NewMeetingParticipantDto;
import ru.avg.server.model.dto.participant.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.mservice.meeting.MeetingMService;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.utils.verifier.Verifier;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MeetingMServiceImpl implements MeetingMService {

    private final MeetingService meetingService;

    private final ParticipantService participantService;

    private final MeetingParticipantService meetingParticipantService;

    private final ParticipantMapper participantMapper;

    private final MeetingMapper meetingMapper;

    private final MeetingParticipantMapper meetingParticipantMapper;

    private final Verifier verifier;

    @Override
    public MeetingDto save(Integer companyId, NewMeetingDto newMeetingDto) {

        MeetingDto meetingDto = meetingService.save(companyId, newMeetingDto);
        Meeting newMeeting = meetingMapper.fromDto(meetingDto);

        updateParticipants(companyId, newMeeting, newMeetingDto.getMeetingParticipants());

        if (newMeetingDto.getChairmanId() != null && newMeetingDto.getSecretaryId() != null &&
                newMeetingDto.getChairmanId().equals(newMeetingDto.getSecretaryId())) {
            throw new MeetingParticipantEqual(newMeetingDto.getChairmanId());
        } else {
            updateChairman(companyId, newMeeting, newMeetingDto.getChairmanId());

            updateSecretary(companyId, newMeeting, newMeetingDto.getSecretaryId());

            meetingService.update(newMeeting, newMeeting);
        }

        return meetingService.findById(companyId, newMeeting.getId());
    }

    public MeetingDto update(Integer companyId, Integer meetingId, MeetingUpdateDto meetingDto) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        Meeting existingMeeting = meetingMapper.fromDto(meetingService.findById(companyId, meetingId));

        Meeting updateSource = meetingMapper.fromUpdateDto(meetingDto);
        updateSource.setId(existingMeeting.getId());

        // Only apply updates for non-null or non-blank fields
        if (Objects.nonNull(updateSource.getType()) && updateSource.getType() != existingMeeting.getType()) {
            // Clear participants when meeting type changes (different quorum/rules may apply)
            meetingParticipantService.deleteByMeetingId(meetingId);
        }

        updateChairman(companyId, existingMeeting, meetingDto.getChairmanId());

        updateSecretary(companyId, existingMeeting, meetingDto.getSecretaryId());

        return meetingService.update(existingMeeting, updateSource);
    }

    private MeetingParticipantDto createMeetingParticipant(Meeting meeting, Integer participantId, Boolean isPresent) {
        MeetingParticipant newMeetingParticipant = MeetingParticipant.builder()
                .meeting(meeting)
                .participant(participantMapper
                        .fromDto(participantService.findById(meeting.getCompany().getId(), participantId)))
                .isPresent(isPresent)
                .build();

        return meetingParticipantMapper.toDto(newMeetingParticipant);
    }

    private void updateParticipants(Integer companyId,
                                    Meeting meeting,
                                    List<NewMeetingParticipantDto> newMeetingParticipants) {
        if (newMeetingParticipants != null) {
            for (NewMeetingParticipantDto participant : newMeetingParticipants) {
                MeetingParticipantDto newParticipant =
                        createMeetingParticipant(meeting, participant.getParticipantId(), participant.getIsPresent());
                meetingParticipantService.save(companyId, meeting.getId(), newParticipant);
            }
        }
    }

    private void updateChairman(Integer companyId, Meeting meeting, Integer chairmanId) {
        if (chairmanId != null) {
            MeetingParticipantDto currentChairman = meetingParticipantService
                    .findByParticipantId(companyId, meeting.getId(), chairmanId);
            if (currentChairman != null) {
                meeting.setChairman(meetingParticipantMapper.fromDto(currentChairman));
            } else {
                MeetingParticipantDto newChairman = createMeetingParticipant(meeting, chairmanId, true);
                MeetingParticipantDto chairman = meetingParticipantService.save(companyId, meeting.getId(), newChairman);
                meeting.setChairman(meetingParticipantMapper.fromDto(chairman));
            }
        }
    }

    private void updateSecretary(Integer companyId, Meeting meeting, Integer secretaryId) {
        if (secretaryId != null) {
            MeetingParticipantDto currentSecretary = meetingParticipantService
                    .findByParticipantId(companyId, meeting.getId(), secretaryId);
            if (currentSecretary != null) {
                meeting.setSecretary(meetingParticipantMapper.fromDto(currentSecretary));
            } else {
                MeetingParticipantDto newSecretary = createMeetingParticipant(meeting, secretaryId, true);
                MeetingParticipantDto secretary = meetingParticipantService.save(companyId, meeting.getId(), newSecretary);
                meeting.setSecretary(meetingParticipantMapper.fromDto(secretary));
            }
        }
    }
}
