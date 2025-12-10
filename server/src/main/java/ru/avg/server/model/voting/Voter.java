package ru.avg.server.model.voting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.model.topic.Topic;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "voter")
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", nullable = false)
    private Voting voting;

    @ManyToOne
    @JoinColumn(name = "meeting_participant_id")
    private MeetingParticipant participant;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "vote")
    @Enumerated(EnumType.STRING)
    private VoteType vote;

    @Column(name = "is_related_party_deal")
    private boolean isRelatedPartyDeal;
}