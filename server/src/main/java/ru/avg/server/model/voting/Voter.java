package ru.avg.server.model.voting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.model.topic.Topic;

/**
 * Entity representing a voter in a voting process.
 * Maps to the 'voter' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - voting: Reference to the parent voting (required)
 * - participant: The meeting participant casting the vote
 * - topic: The topic being voted on
 * - vote: The selected vote option (e.g., FOR, AGAINST)
 * - isRelatedPartyDeal: Indicates if the vote involves a related-party transaction
 *
 * Enforces referential integrity with Voting, MeetingParticipant, and Topic entities.
 */
@Entity
@Table(name = "voter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", nullable = false)
    private Voting voting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_participant_id", nullable = false)
    private MeetingParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "vote")
    @Enumerated(EnumType.STRING)
    private VoteType vote;

    @Column(name = "is_related_party_deal")
    private boolean isRelatedPartyDeal;
}