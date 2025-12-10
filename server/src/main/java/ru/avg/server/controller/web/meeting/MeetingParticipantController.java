package ru.avg.server.controller.web.meeting;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.MeetingParticipantCreationDto;
import ru.avg.server.model.dto.MeetingParticipantDto;
import ru.avg.server.service.participant.MeetingParticipantService;

import java.util.List;

@RestController
@RequestMapping("/approval/{companyId}/meeting/{meetingId}/participants")
@AllArgsConstructor
@Slf4j
public class MeetingParticipantController {

    private final MeetingParticipantService meetingParticipantService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetingParticipantDto> findParticipants(@PathVariable("companyId") Integer companyId,
                                                        @PathVariable("meetingId") Integer meetingId) {
        log.info("show Participants by meetingId {}", meetingId);
        return meetingParticipantService.findAll(meetingId);
    }

    @GetMapping("/{participantId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingParticipantDto findParticipant(@PathVariable("companyId") Integer companyId,
                                                 @PathVariable("meetingId") Integer meetingId,
                                                 @PathVariable("participantId") Integer participantId) {
        log.info("show participant by meetingId: {} and participant id: {}", meetingId, participantId);
        return meetingParticipantService.findByParticipantId(meetingId, participantId);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetingParticipantDto> saveMeetingParticipant(@PathVariable("companyId") Integer companyId,
                                                              @PathVariable("meetingId") Integer meetingId,
                                                              @RequestBody @Valid MeetingParticipantCreationDto creationDto,
                                                              BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("save meeting participant: {}", creationDto);
        return meetingParticipantService.save(creationDto.getPotentialParticipants());
    }

    @DeleteMapping("/{participantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMeetingParticipant(@PathVariable("companyId") Integer companyId,
                                         @PathVariable("meetingId") Integer meetingId,
                                         @PathVariable("participantId") Integer participantId) {
        log.info("remove meeting participant by meetingId {}", meetingId);
        meetingParticipantService.delete(meetingParticipantService.findByParticipantId(meetingId, participantId).getId());
    }
}