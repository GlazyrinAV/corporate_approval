package ru.avg.server.repository.company;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.company.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    void deleteCompanyById(Integer id);
}