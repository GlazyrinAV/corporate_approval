package ru.avg.server.controller.web.participant;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.ParticipantDto;
import ru.avg.server.service.participant.ParticipantService;

import java.util.List;

@RestController
@RequestMapping("/approval/{companyId}/participant")
@AllArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipantDto> showAllParticipants(@PathVariable("companyId") Integer companyId) {
        log.info("show all Participants by companyId {}", companyId);
        return participantService.findAll(companyId);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantDto save(@PathVariable("companyId") Integer companyId,
                               @Valid @RequestBody ParticipantDto participantDto,
                               BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("save participant: {}", participantDto);
        return participantService.save(participantDto);
    }

    @GetMapping("/{participantId}")
    @ResponseStatus(HttpStatus.OK)
    public ParticipantDto findParticipant(@PathVariable("companyId") Integer companyId,
                                          @PathVariable("participantId") Integer participantId) {
        log.info("get participant: {}", participantId);
        return participantService.findById(participantId);
    }

    @PostMapping("/{participantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantDto updateParticipant(@PathVariable("companyId") Integer companyId,
                                            @PathVariable("participantId") Integer participantId,
                                            @Valid @RequestBody ParticipantDto newParticipantDto,
                                            BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("save update for participant:  {}", newParticipantDto);
        return participantService.update(participantId, newParticipantDto);
    }

    @DeleteMapping("/{participantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(@PathVariable("companyId") Integer companyId,
                                  @PathVariable("participantId") Integer participantId) {
        log.info("remove Participant by id: {}", participantId);
        participantService.delete(participantId);
    }
}