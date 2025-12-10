package ru.avg.server.model.dto.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyTypeNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.company.CompanyType;
import ru.avg.server.model.dto.CompanyDto;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class CompanyMapper {

    public Company fromDto(CompanyDto companyDto) {
        return Company.builder()
                .companyType(Arrays.stream(CompanyType.values()).filter(x -> x.getTitle()
                                .equals(companyDto.getCompanyType())).findFirst()
                        .orElseThrow(() -> new CompanyTypeNotFound(companyDto.getCompanyType())))
                .title(companyDto.getTitle())
                .inn(companyDto.getInn())
                .hasBoardOfDirectors(companyDto.getHasBoardOfDirectors())
                .id(companyDto.getId())
                .build();
    }

    public CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .companyType(company.getCompanyType().getTitle())
                .title(company.getTitle())
                .inn(company.getInn())
                .hasBoardOfDirectors(company.getHasBoardOfDirectors())
                .id(company.getId())
                .build();
    }
}