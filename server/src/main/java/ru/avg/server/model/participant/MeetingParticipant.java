package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.meeting.Meeting;

/**
 * Entity representing a participant's membership in a meeting.
 * Maps to the 'meeting_participant' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - meeting: Reference to the associated meeting (required)
 * - participant: Reference to the participant (required)
 * - isPresent: Attendance status (true = present, false = absent)
 *
 * Enforces referential integrity with Meeting and Participant entities.
 * Ensures that each participant in a meeting has a clear attendance status.
 */
@Entity
@Table(name = "meeting_participant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(name = "is_present", nullable = false)
    private boolean isPresent;
}