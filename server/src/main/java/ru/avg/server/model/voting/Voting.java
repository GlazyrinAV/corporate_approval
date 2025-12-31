package ru.avg.server.model.voting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.topic.Topic;

import java.util.List;

/**
 * Entity representing a voting session conducted on a meeting topic.
 * Maps to the 'voting' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - topic: The topic being voted on (required)
 * - isAccepted: Final outcome of the vote (true = accepted, false = rejected)
 * - voters: List of participants who cast votes
 *
 * Enforces referential integrity with Topic and Voter entities.
 * Uses lazy loading for voters to prevent performance issues during queries.
 */
@Entity
@Table(name = "voting")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "is_accepted", nullable = false)
    private boolean isAccepted;

    @OneToMany(mappedBy = "voting", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voter> voters;

    @Override
    public String toString() {
        return "Voting{" +
                "id=" + id +
                ", topic=" + topic +
                ", isAccepted=" + isAccepted +
                ", votersCount=" + (voters != null ? voters.size() : 0) +
                '}';
    }
}