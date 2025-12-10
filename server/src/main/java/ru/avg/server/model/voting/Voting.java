package ru.avg.server.model.voting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.topic.Topic;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "voting")
public class Voting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @OneToMany(mappedBy = "voting", fetch = FetchType.LAZY)
    private List<Voter> voters;

    @Override
    public String toString() {
        return "Voting{" +
                "isAccepted=" + isAccepted +
                ", topic=" + topic +
                ", id=" + id +
                '}';
    }
}