package ru.avg.server.model.dto.company.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.dto.company.CompanyDto;

/**
 * Mapper component responsible for bidirectional conversion between {@link Company} entities
 * and {@link CompanyDto} data transfer objects.
 * <p>
 * This class provides methods to transform data between the persistence layer (entity)
 * and the API layer (DTO), ensuring proper mapping of complex attributes such as
 * {@link ru.avg.server.model.company.CompanyType}. It is registered as a Spring component
 * using {@link Component} and uses constructor injection via {@link RequiredArgsConstructor}.
 * </p>
 *
 * @see Company
 * @see CompanyDto
 * @author AVG
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {

    /**
     * Converts a {@link CompanyDto} object into a {@link Company} entity.
     * <p>
     * This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code companyType} string into the proper {@link ru.avg.server.model.company.CompanyType} enum.
     * It performs validation to ensure the input DTO is not null.
     * </p>
     *
     * @param companyDto the DTO containing company data, must not be {@code null}
     * @return a fully constructed {@link Company} entity with mapped values
     * @throws IllegalArgumentException if {@code companyDto} is {@code null}
     *
     * @see ru.avg.server.model.company.CompanyType
     */
    public Company fromDto(CompanyDto companyDto) {
        if (companyDto == null) {
            throw new IllegalArgumentException("CompanyDto must not be null");
        }

        return Company.builder()
                .id(companyDto.getId())
                .title(companyDto.getTitle())
                .inn(companyDto.getInn())
                .companyType(ru.avg.server.model.company.CompanyType.valueOf(companyDto.getCompanyType()))
                .hasBoardOfDirectors(companyDto.getHasBoardOfDirectors())
                .build();
    }

    /**
     * Converts a {@link Company} entity into a {@link CompanyDto} object.
     * <p>
     * This method maps the fields from the entity to the corresponding fields in the DTO,
     * converting the {@link ru.avg.server.model.company.CompanyType} enum back to its string representation.
     * It performs validation to ensure the input entity is not null.
     * </p>
     *
     * @param company the entity containing company data, must not be {@code null}
     * @return a fully constructed {@link CompanyDto} with mapped values
     * @throws IllegalArgumentException if {@code company} is {@code null}
     */
    public CompanyDto toDto(Company company) {
        if (company == null) {
            throw new IllegalArgumentException("Company must not be null");
        }

        return CompanyDto.builder()
                .id(company.getId())
                .title(company.getTitle())
                .inn(company.getInn())
                .companyType(company.getCompanyType().name())
                .hasBoardOfDirectors(company.getHasBoardOfDirectors())
                .build();
    }
}