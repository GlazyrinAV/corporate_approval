package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.meeting.MeetingRepository;

@Component
@RequiredArgsConstructor
public class MeetingParticipantMapper {

    private final MeetingRepository meetingRepository;

    private final ParticipantMapper participantMapper;

    public MeetingParticipant fromDto(MeetingParticipantDto dto) {
        if (dto.getIsPresent() == null) {
            dto.setIsPresent(false);
        }
        return MeetingParticipant.builder()
                .id(dto.getId())
                .isPresent(dto.getIsPresent())
                .meeting(meetingRepository.findById(dto.getMeetingId())
                        .orElseThrow(() -> new MeetingNotFound(dto.getMeetingId())))
                .participant(participantMapper.fromDto(dto.getParticipant()))
                .build();
    }

    public MeetingParticipantDto toDto(MeetingParticipant participant) {
        return MeetingParticipantDto.builder()
                .id(participant.getId())
                .meetingId(participant.getMeeting().getId())
                .isPresent(participant.isPresent())
                .participant(participantMapper.toDto(participant.getParticipant()))
                .build();
    }

    public MeetingParticipantDto fromParticipantDto(ParticipantDto participant) {
        return MeetingParticipantDto.builder()
                .id(participant.getId())
                .participant(participant)
                .isPresent(false)
                .build();
    }
}