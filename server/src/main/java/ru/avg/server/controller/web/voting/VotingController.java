package ru.avg.server.controller.web.voting;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.VotingCreationDto;
import ru.avg.server.model.dto.VotingDto;
import ru.avg.server.service.voting.VotingService;

@RestController
@RequestMapping("/approval/{companyId}/{meetingId}/{topicId}/voting")
@AllArgsConstructor
@Slf4j
public class VotingController {

    private final VotingService votingService;

    @PostMapping("/make_vote")
    @ResponseStatus(HttpStatus.CREATED)
    public VotingDto saveVote(@PathVariable Integer companyId,
                              @PathVariable Integer meetingId,
                              @PathVariable Integer topicId,
                              @Valid @RequestBody VotingCreationDto voters,
                              BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }

        log.info("Saving new vote: {}", voters);
        return votingService.makeVote(topicId, voters.getVoters());
    }
}