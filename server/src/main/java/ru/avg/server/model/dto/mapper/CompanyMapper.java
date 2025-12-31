package ru.avg.server.model.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyTypeNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.company.CompanyType;
import ru.avg.server.model.dto.CompanyDto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between {@link Company} entities and {@link CompanyDto} objects.
 * This class is a Spring component and is intended to be injected and used by services that require such conversions.
 * It provides bidirectional mapping with efficient lookup of {@link CompanyType} using a static map.
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {

    /**
     * Pre-built immutable map for fast O(1) lookup of {@link CompanyType} by its title.
     * Initialized at class loading time using all values from the {@link CompanyType} enum.
     * Used to avoid linear search during DTO-to-entity conversion, improving performance.
     */
    private static final Map<String, CompanyType> COMPANY_TYPE_MAP = Arrays.stream(CompanyType.values())
            .collect(Collectors.toMap(CompanyType::getTitle, Function.identity()));

    /**
     * Converts a {@link CompanyDto} to a {@link Company} entity.
     * The company type is resolved by matching the DTO's type title with the corresponding enum value
     * using a pre-built map for efficient lookup.
     *
     * @param companyDto the DTO to convert; must not be null
     * @return the fully populated {@link Company} entity
     * @throws CompanyTypeNotFound if no {@link CompanyType} exists with the given title in the DTO
     */
    public Company fromDto(CompanyDto companyDto) {
        CompanyType companyType = COMPANY_TYPE_MAP.get(companyDto.getCompanyType());
        if (companyType == null) {
            throw new CompanyTypeNotFound(companyDto.getCompanyType());
        }

        return Company.builder()
                .id(companyDto.getId())
                .title(companyDto.getTitle())
                .inn(companyDto.getInn())
                .companyType(companyType)
                .hasBoardOfDirectors(companyDto.getHasBoardOfDirectors())
                .build();
    }

    /**
     * Converts a {@link Company} entity to a {@link CompanyDto}.
     * The company type is mapped to its title string for representation in the DTO.
     *
     * @param company the entity to convert; must not be null
     * @return the corresponding {@link CompanyDto} with all relevant fields populated
     */
    public CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .title(company.getTitle())
                .inn(company.getInn())
                .companyType(company.getCompanyType().getTitle())
                .hasBoardOfDirectors(company.getHasBoardOfDirectors())
                .build();
    }
}