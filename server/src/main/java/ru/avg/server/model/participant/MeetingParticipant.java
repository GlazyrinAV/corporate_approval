package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.meeting.Meeting;

/**
 * Entity representing the association between a participant and a meeting, including attendance status.
 * This junction entity maps to the 'meeting_participant' database table and implements a many-to-many
 * relationship between {@link Meeting} and {@link Participant} entities, while also storing additional
 * metadata about the participant's attendance.
 * <p>
 * The entity enforces referential integrity through foreign key constraints and ensures that:
 * <ul>
 *   <li>Each meeting-participant relationship has a unique identifier</li>
 *   <li>The meeting and participant associations are mandatory</li>
 *   <li>The attendance status is explicitly recorded</li>
 * </ul>
 * </p>
 *
 * @see Meeting
 * @see Participant
 * @author AVG
 * @since 1.0
 */
@Entity
@Table(name = "meeting_participant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipant {

    /**
     * Unique identifier for the meeting-participant relationship.
     * Generated automatically by the database using identity strategy.
     * Cannot be null and cannot be modified after creation.
     *
     * @see GenerationType#IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    /**
     * Reference to the meeting in which the participant is involved.
     * Establishes a many-to-one relationship with the Meeting entity.
     * Maps to 'meeting_id' column in the database and is mandatory (cannot be null).
     * <p>
     * This field represents the owning side of the relationship, where the
     * meeting_participant table contains a foreign key to the meeting table.
     * </p>
     *
     * @see Meeting
     * @see JoinColumn
     */
    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    /**
     * Reference to the participant involved in the meeting.
     * Establishes a many-to-one relationship with the Participant entity.
     * Maps to 'participant_id' column in the database and is mandatory (cannot be null).
     * <p>
     * This field represents the owning side of the relationship, where the
     * meeting_participant table contains a foreign key to the participant table.
     * </p>
     *
     * @see Participant
     * @see JoinColumn
     */
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    /**
     * Attendance status of the participant in the meeting.
     * Represents whether the participant was present (true) or absent (false) during the meeting.
     * Stored as a boolean in the 'is_present' database column and is mandatory (cannot be null).
     * <p>
     * This field captures the actual attendance information, which may differ from
     * simply being assigned to a meeting. It's used for quorum calculations, voting rights,
     * and meeting minutes generation.
     * </p>
     */
    @Column(name = "is_present", nullable = false)
    private boolean isPresent;
}