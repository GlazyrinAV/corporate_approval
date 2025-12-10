package ru.avg.server.controller.web.topic;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.TopicDto;
import ru.avg.server.service.topic.TopicService;

import java.util.List;

@RestController
@RequestMapping("/approval/{companyId}/meeting/{meetingId}/topic")
@AllArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<TopicDto> showAll(@PathVariable("companyId") Integer companyId,
                                  @PathVariable("meetingId") Integer meetingId) {
        log.info("Showing all topics for meeting id: {}", meetingId);
        return topicService.findAllByMeeting_Id(meetingId);
    }

    @GetMapping("/{topicId}")
    @ResponseStatus(HttpStatus.OK)
    public TopicDto findById(@PathVariable("companyId") Integer companyId,
                             @PathVariable("meetingId") Integer meetingId,
                             @PathVariable("topicId") Integer topicId) {
        log.info("Showing topic by id: {}", topicId);
        return topicService.findById(topicId);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public TopicDto save(@PathVariable("companyId") Integer companyId,
                         @PathVariable("meetingId") Integer meetingId,
                         @RequestBody @Valid TopicDto topicDto,
                         BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors found");
        }
        log.info("Saving new topic: {}", topicDto);
        return topicService.save(topicDto);
    }

    @PostMapping("/{topicId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TopicDto update(@PathVariable("companyId") Integer companyId,
                           @PathVariable("meetingId") Integer meetingId,
                           @PathVariable("topicId") Integer topicId,
                           @Valid @RequestBody TopicDto topicDto,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors found");
        }
        log.info("Updating topic: {}", topicDto);
        return topicService.edit(topicId, topicDto);
    }

    @DeleteMapping("/{topicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") Integer companyId,
                       @PathVariable("meetingId") Integer meetingId,
                       @PathVariable("topicId") Integer topicId) {
        log.info("Deleting topic: {}", topicId);
        topicService.delete(topicId);
    }
}