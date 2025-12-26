package ru.avg.server.service.company.iplm;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.dto.CompanyDto;
import ru.avg.server.model.dto.mapper.CompanyMapper;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.service.company.CompanyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository storage;

    private final CompanyMapper companyMapper;

    @Override
    public CompanyDto save(CompanyDto companyDto) {
        Company newCompany = storage.save(companyMapper.fromDto(companyDto));
        return companyMapper.toDto(newCompany);
    }

    @Override
    public CompanyDto update(Integer id, CompanyDto companyDto) {
        Company company = storage.findById(id)
                .orElseThrow(() -> new CompanyNotFound(id));
        Company newCompany = companyMapper.fromDto(companyDto);

        if (!newCompany.getTitle().isBlank()) {
            company.setTitle(newCompany.getTitle());
        }
        if (newCompany.getInn() != null) {
            company.setInn(newCompany.getInn());
        }
        if (newCompany.getCompanyType() != null) {
            company.setCompanyType(newCompany.getCompanyType());
        }
        if (newCompany.getHasBoardOfDirectors() != null) {
            company.setHasBoardOfDirectors(newCompany.getHasBoardOfDirectors());
        }
        return companyMapper.toDto(storage.save(company));
    }

    @Override
    @Transactional
    public void delete(Integer companyId) {
        storage.deleteCompanyById(companyId);
    }

    @Override
    public CompanyDto findById(Integer companyId) {
        return companyMapper.toDto(storage.findById(companyId)
                .orElseThrow(() -> new CompanyNotFound(companyId)));
    }

    @Override
    public List<CompanyDto> findAll() {
        return storage.findAll().stream()
                .map(companyMapper::toDto)
                .toList();
    }
}