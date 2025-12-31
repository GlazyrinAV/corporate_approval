package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.company.Company;

/**
 * Entity representing a participant in a company, such as a shareholder or member.
 * Maps to the 'participant' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - name: Full name of the participant
 * - share: Ownership percentage or share value
 * - company: Reference to the owning company (required)
 * - type: Classification of the participant (e.g., Founder, Investor)
 * - isActive: Indicates whether the participant is currently active
 *
 * Enforces referential integrity with the Company entity.
 */
@Entity
@Table(name = "participant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "share", nullable = false, precision = 5)
    private Double share;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}