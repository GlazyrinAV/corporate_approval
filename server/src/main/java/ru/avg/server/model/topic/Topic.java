package ru.avg.server.model.topic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.meeting.Meeting;

/**
 * Entity representing a topic discussed in a meeting.
 * Maps to the 'topic' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - title: Subject or agenda item of the topic
 * - meeting: Reference to the parent meeting (required)
 *
 * Enforces referential integrity with the Meeting entity.
 */
@Entity
@Table(name = "topic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;
}