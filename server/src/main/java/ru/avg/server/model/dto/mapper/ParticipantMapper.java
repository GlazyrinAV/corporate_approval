package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.participant.ParticipantTypeNotFound;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;
import ru.avg.server.repository.company.CompanyRepository;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class ParticipantMapper {

    private final CompanyRepository companyRepository;

    public Participant fromDto(ParticipantDto participantDto) throws CompanyNotFound {
        ParticipantType type = Arrays.stream(ParticipantType.values()).filter(x ->
                        x.getTitle().equals(participantDto.getType())).findFirst()
                .orElseThrow(() -> new ParticipantTypeNotFound(participantDto.getType()));
        return Participant.builder()
                .name(participantDto.getName())
                .share(participantDto.getShare())
                .type(type)
                .id(participantDto.getId())
                .company(companyRepository.findById(participantDto.getCompanyId())
                        .orElseThrow(() -> new CompanyNotFound(participantDto.getCompanyId())))
                .isACtive(participantDto.getIsActive())
                .build();
    }

    public ParticipantDto toDto(Participant participant) {
        return ParticipantDto.builder()
                .id(participant.getId())
                .name(participant.getName())
                .share(participant.getShare())
                .companyId(participant.getCompany().getId())
                .type(participant.getType().getTitle())
                .isActive(participant.getIsACtive())
                .build();
    }
}