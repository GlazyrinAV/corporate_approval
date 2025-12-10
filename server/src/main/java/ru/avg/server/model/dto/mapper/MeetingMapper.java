package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.ParticipantNotFound;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.participant.ParticipantRepository;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class MeetingMapper {

    private final CompanyRepository companyRepository;

    private final ParticipantRepository participantRepository;

    public Meeting fromDto(MeetingDto dto) {
        Participant chairman;
        Participant secretary;
        if (dto.getChairmanId() != null) {
            chairman = participantRepository.findById(dto.getChairmanId())
                    .orElseThrow(() -> new ParticipantNotFound(dto.getId()));
        } else {
            chairman = null;
        }
        if (dto.getSecretaryId() != null) {
            secretary = participantRepository.findById(dto.getSecretaryId())
                    .orElseThrow(() -> new ParticipantNotFound(dto.getSecretaryId()));
        } else {
            secretary = null;
        }
        return Meeting.builder()
                .id(dto.getId())
                .chairman(chairman)
                .address(dto.getAddress())
                .type(Arrays.stream(MeetingType.values()).filter(x -> x.getTitle().equals(dto.getType()))
                        .findFirst().orElseThrow(() -> new MeetingTypeNotFound(dto.getType())))
                .date(dto.getDate())
                .secretary(secretary)
                .company(companyRepository.findById(dto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(dto.getCompanyId())))
                .build();
    }

    public MeetingDto toDto(Meeting meeting) {
        Integer chairmanId;
        Integer secretaryId;
        if (meeting.getChairman() != null) {
            chairmanId = meeting.getChairman().getId();
        } else {
            chairmanId = null;
        }
        if (meeting.getSecretary() != null) {
            secretaryId = meeting.getSecretary().getId();
        } else {
            secretaryId = null;
        }
        return MeetingDto.builder()
                .id(meeting.getId())
                .chairmanId(chairmanId)
                .address(meeting.getAddress())
                .type(meeting.getType().getTitle())
                .date(meeting.getDate())
                .secretaryId(secretaryId)
                .companyId(meeting.getCompany().getId())
                .build();
    }
}