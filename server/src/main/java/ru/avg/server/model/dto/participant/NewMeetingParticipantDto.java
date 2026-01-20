package ru.avg.server.model.dto.participant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for adding participants to a meeting.
 * This class is used as input when associating participants with a meeting, typically in POST requests.
 * It contains a list of participant data transfer objects that will be processed to establish
 * meeting-participant relationships.
 * <p>
 * The DTO uses Jakarta Validation annotations to ensure data integrity:
 * <ul>
 *   <li>{@link NotNull} ensures the list itself is not null</li>
 *   <li>{@link Valid} ensures each contained {@link MeetingParticipantDto} is validated</li>
 * </ul>
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction.
 * </p>
 *
 * @author AVG
 * @see MeetingParticipantDto
 * @since 1.0
 */
@Data
@Builder
public class NewMeetingParticipantDto {

    /**
     * List of potential participants to be added to a meeting.
     * This field must not be null and should contain valid {@link MeetingParticipantDto} objects.
     * Each participant in the list will be validated individually due to the {@link Valid} annotation.
     * Initialized with an empty ArrayList by default to prevent null pointer exceptions.
     *
     * @see Valid
     * @see NotNull
     * @see MeetingParticipantDto
     */
    @Valid
    @NotNull(message = "List of potential participants must not be null")
    private List<@Valid MeetingParticipantDto> potentialParticipants = new ArrayList<>();
}