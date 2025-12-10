package ru.avg.server.model.meeting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.participant.Participant;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "secretary_id")
    private Participant secretary;

    @ManyToOne
    @JoinColumn(name = "chairman_id")
    private Participant chairman;
}