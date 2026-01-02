package ru.avg.server.model.dto.company.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyTypeNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.company.CompanyType;
import ru.avg.server.model.dto.company.NewCompanyDto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper component responsible for converting {@link NewCompanyDto} to a {@link Company} entity.
 * <p>This class provides methods to transform data from the DTO layer (used in API requests)
 * into the domain model layer (used internally by the application).</p>
 *
 * <p>The mapping process includes:
 * <ul>
 *   <li>Resolving the company type string from the DTO to a corresponding {@link CompanyType} enum value.</li>
 *   <li>Throwing a {@link CompanyTypeNotFound} exception if the provided company type is invalid.</li>
 *   <li>Constructing a new {@link Company} instance using the builder pattern with mapped values.</li>
 * </ul>
 * </p>
 *
 * <p>This mapper is designed to be used as a Spring-managed bean and is annotated with {@link Component}.</p>
 *
 * @see NewCompanyDto
 * @see Company
 * @see CompanyType
 * @author AVG
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NewCompanyMapper {

    /**
     * A static map that associates company type titles (as strings) with their corresponding {@link CompanyType} enum values.
     * This map is used to efficiently resolve the company type during the mapping process.
     * <p>
     * The map is built from all values of the {@link CompanyType} enum, using the {@code title} field as the key.
     * It allows for fast lookup when converting from a string-based company type in the DTO to the enum in the entity.
     * </p>
     */
    private static final Map<String, CompanyType> COMPANY_TYPE_MAP = Arrays.stream(CompanyType.values())
            .collect(Collectors.toMap(CompanyType::getTitle, Function.identity()));

    /**
     * Converts a {@link NewCompanyDto} object into a {@link Company} entity.
     * <p>This method maps the fields from the DTO to the corresponding fields in the entity,
     * resolving the {@code companyType} string into a proper {@link CompanyType} enum value.</p>
     *
     * <p>If the provided company type title does not match any known {@link CompanyType},
     * a {@link CompanyTypeNotFound} exception is thrown.</p>
     *
     * @param companyDto the DTO containing company data, must not be {@code null}
     * @return a fully constructed {@link Company} entity with mapped values
     * @throws CompanyTypeNotFound if the {@code companyType} in the DTO does not correspond to any known company type
     * @throws NullPointerException if {@code companyDto} is {@code null}
     *
     * @see CompanyType
     * @see CompanyTypeNotFound
     */
    public Company fromDto(NewCompanyDto companyDto) {
        if (companyDto == null) {
            throw new IllegalArgumentException("NewCompanyDto must not be null");
        }

        CompanyType companyType = COMPANY_TYPE_MAP.get(companyDto.getCompanyType());
        if (companyType == null) {
            throw new CompanyTypeNotFound(companyDto.getCompanyType());
        }

        return Company.builder()
                .title(companyDto.getTitle())
                .inn(companyDto.getInn())
                .companyType(companyType)
                .hasBoardOfDirectors(companyDto.getHasBoardOfDirectors())
                .build();
    }
}