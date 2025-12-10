package ru.avg.server.model.company;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "inn")
    private Long inn;

    @Column(name = "company_type")
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Column(name = "has_board_of_directors")
    private Boolean hasBoardOfDirectors;
}