package ru.avg.server.model.topic;

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
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
}