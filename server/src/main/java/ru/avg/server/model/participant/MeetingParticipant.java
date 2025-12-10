package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.meeting.Meeting;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meeting_participant")
public class MeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(name = "is_present")
    private boolean isPresent;
}