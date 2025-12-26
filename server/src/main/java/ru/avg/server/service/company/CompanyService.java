package ru.avg.server.service.company;

import ru.avg.server.model.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

    CompanyDto save(CompanyDto companyDto);

    CompanyDto update(Integer companyId, CompanyDto companyDto);

    void delete(Integer companyId);

    CompanyDto findById(Integer companyId);

    List<CompanyDto> findAll();
}