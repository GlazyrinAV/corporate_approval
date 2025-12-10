package ru.avg.server.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.avg.server.model.company.Company;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "share")
    private Double share;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    @Column(name = "is_active")
    private Boolean isACtive;
}