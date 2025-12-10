package ru.avg.server.controller.web.meeting;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.service.meeting.MeetingService;

import java.util.List;

@RestController
@RequestMapping("/approval/{companyId}/meeting")
@AllArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetingDto> showAllMeetings(@PathVariable("companyId") Integer companyId) {
        log.info("show all Meetings by companyId: {}", companyId);
        return meetingService.findAll(companyId);
    }

    @GetMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingDto showMeeting(@PathVariable("companyId") Integer companyId,
                                  @PathVariable("meetingId") Integer meetingId) {
        log.info("show Meeting by meetingId: {}", meetingId);
        return meetingService.findById(meetingId);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingDto save(@PathVariable("companyId") Integer companyId,
                           @Valid @RequestBody MeetingDto meetingDto,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("save meeting: {}", meetingDto);
        return meetingService.save(meetingDto);
    }

    @PostMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingDto update(@PathVariable("companyId") Integer companyId,
                             @PathVariable("meetingId") Integer meetingId,
                             @RequestBody @Valid MeetingDto newMeetingDto,
                             BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("update meeting: {}", newMeetingDto);
        return meetingService.update(meetingId, newMeetingDto);
    }

    @DeleteMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") Integer companyId,
                       @PathVariable("meetingId") Integer meetingId) {
        log.info("delete meeting by meetingId: {}", meetingId);
        meetingService.delete(meetingId);
    }
}