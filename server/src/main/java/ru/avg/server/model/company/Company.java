package ru.avg.server.model.company;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a company in the system.
 * Maps to the 'company' database table.
 *
 * Key attributes:
 * - id: Auto-generated unique identifier
 * - title: Company name (business title)
 * - inn: Tax identification number (10-digit)
 * - companyType: Classification of the company (e.g., LLC, JSC)
 * - hasBoardOfDirectors: Indicates if company has a board of directors
 */
@Entity
@Table(name = "company")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "inn", nullable = false, unique = true)
    private Long inn;

    @Column(name = "company_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Column(name = "has_board_of_directors", nullable = false)
    private Boolean hasBoardOfDirectors;
}