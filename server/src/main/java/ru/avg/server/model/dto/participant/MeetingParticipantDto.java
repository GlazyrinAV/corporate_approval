package ru.avg.server.model.dto.participant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing the association between a participant and a meeting.
 * This class is used to transfer data about meeting-participant relationships between
 * different layers of the application, particularly during operations that manage
 * participant attendance and voting rights in meetings.
 * <p>
 * The DTO captures both the structural relationship (which participant is in which meeting)
 * and the dynamic attendance status (whether the participant was present). It serves as
 * input for adding participants to meetings and as output when retrieving meeting attendance
 * information.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, facilitating easy creation of instances
 * especially in test scenarios and service implementations.
 * </p>
 *
 * @see ParticipantDto
 * @see ru.avg.server.model.participant.MeetingParticipant
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
public class MeetingParticipantDto {

    /**
     * Unique identifier for the meeting-participant association.
     * This field is optional and is typically null when creating a new association,
     * as the ID is assigned by the system upon persistence. After creation,
     * this field contains the generated ID that can be used for subsequent
     * operations such as updates or deletions.
     * <p>
     * For update operations, the ID must match an existing meeting-participant
     * relationship record to ensure proper targeting of the update operation.
     * </p>
     */
    private Integer id;

    /**
     * The identifier of the meeting to which the participant is being associated.
     * This field establishes the parent relationship between the participant and
     * the meeting, ensuring proper scoping and multi-tenancy enforcement.
     * The meetingId must not be null, ensuring referential integrity at the DTO level.
     * <p>
     * This field is mandatory for all operations involving meeting-participant
     * associations, as it provides the context for where the participant will
     * be attending. The ID must correspond to an existing meeting record
     * within the same company context.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see ru.avg.server.model.meeting.Meeting
     */
    @NotNull(message = "Meeting ID must not be null")
    private Integer meetingId;

    /**
     * The participant details being associated with the meeting.
     * This field contains the complete participant information including
     * their identifier, name, type, and other attributes.
     * The participant must not be null, ensuring that only valid participants
     * can be added to meetings.
     * <p>
     * The {@link Valid} annotation ensures that the nested ParticipantDto
     * is also validated according to its own constraints when this DTO
     * is validated. This creates a cascading validation effect,
     * ensuring data integrity throughout the object graph.
     * </p>
     *
     * @see ParticipantDto
     * @see jakarta.validation.Valid
     * @see jakarta.validation.constraints.NotNull
     */
    @Valid
    @NotNull(message = "Participant must not be null")
    private Integer participantId;

    /**
     * The attendance status of the participant in the meeting.
     * Represents whether the participant was present (true) or absent (false)
     * during the meeting. Defaults to false if not explicitly provided.
     * <p>
     * This field is used to track actual attendance, which may differ from
     * simply being assigned to a meeting. It's crucial for calculating
     * quorum, determining voting rights, and generating accurate meeting minutes.
     * The Boolean wrapper type allows for three states: true (present),
     * false (absent), and null (status not yet determined).
     * </p>
     */
    private Boolean isPresent = false;
}