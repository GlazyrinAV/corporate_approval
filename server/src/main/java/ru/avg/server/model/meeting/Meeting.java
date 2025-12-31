package ru.avg.server.model.meeting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.participant.Participant;

import java.time.LocalDate;

/**
 * Entity representing a meeting within the system.
 * Maps to the 'meeting' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - company: The company organizing the meeting (foreign key)
 * - type: Classification of the meeting (e.g., Annual, Extraordinary)
 * - date: The date when the meeting takes place
 * - address: Physical location of the meeting
 * - secretary and chairman: Key participants assigned to roles
 *
 * Ensures referential integrity through relationships with Company and Participant entities.
 */
@Entity
@Table(name = "meeting")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "address", nullable = false, length = 512)
    private String address;

    @ManyToOne
    @JoinColumn(name = "secretary_id")
    private Participant secretary;

    @ManyToOne
    @JoinColumn(name = "chairman_id")
    private Participant chairman;
}