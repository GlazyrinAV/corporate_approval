package ru.avg.server.service.company.iplm;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.model.company.Company;
import ru.avg.server.model.dto.company.CompanyDto;
import ru.avg.server.model.dto.company.NewCompanyDto;
import ru.avg.server.model.dto.company.mapper.CompanyMapper;
import ru.avg.server.model.dto.company.mapper.NewCompanyMapper;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.service.company.CompanyService;
import ru.avg.server.utils.updater.Updater;

import java.util.List;

/**
 * Implementation of {@link CompanyService} providing business logic for managing company entities.
 * This service handles creation, retrieval, update, and deletion of companies using database operations
 * via {@link CompanyRepository} and DTO mapping via {@link CompanyMapper}.
 *
 * <p>The class is annotated with {@link Service} to indicate it's a Spring service component,
 * and uses {@link RequiredArgsConstructor} to generate a constructor for dependency injection.</p>
 *
 * <p>This implementation supports partial updates (PATCH semantics), where only non-null or non-blank
 * fields from the update DTO are applied to the existing entity. The actual field-level merging
 * is delegated to the {@link Updater} utility class.</p>
 *
 * @author AVG
 * @see CompanyService
 * @see CompanyRepository
 * @see CompanyMapper
 * @see NewCompanyMapper
 * @see Updater
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    /**
     * Repository used for data persistence operations on {@link Company} entities.
     * Injected via constructor by Spring due to {@link RequiredArgsConstructor}.
     */
    private final CompanyRepository storage;

    /**
     * Mapper responsible for converting between {@link Company} entities and {@link CompanyDto} objects.
     * Used to transform persistent domain models into data transfer objects for external exposure.
     */
    private final CompanyMapper companyMapper;

    /**
     * Mapper responsible for converting {@link NewCompanyDto} objects into {@link Company} entities.
     * Used when creating new companies from API input data.
     */
    private final NewCompanyMapper newCompanyMapper;

    /**
     * Utility component used to perform partial updates on entity objects.
     * Applies non-null and non-blank fields from the source entity to the target entity.
     */
    private final Updater updater;

    /**
     * Saves a new company to the database.
     * <p>Converts the incoming DTO to a domain entity using {@link NewCompanyMapper},
     * persists it via the repository, and returns the saved data as a DTO using {@link CompanyMapper}.</p>
     *
     * @param newCompanyDto the DTO containing company data (must not be null)
     * @return the saved CompanyDto with generated ID and system-assigned fields
     * @throws IllegalArgumentException                            if newCompanyDto is null
     * @throws ru.avg.server.exception.company.CompanyTypeNotFound if the specified company type is not supported
     * @see NewCompanyMapper#fromDto(NewCompanyDto)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto save(NewCompanyDto newCompanyDto) {
        Company company = newCompanyMapper.fromDto(newCompanyDto);
        Company savedCompany = storage.save(company);
        return companyMapper.toDto(savedCompany);
    }

    /**
     * Updates an existing company with non-null or non-blank fields from the provided DTO.
     * Only the fields that are present in the DTO are updated (partial update semantics).
     * <p>The actual field comparison and copying is handled by the {@link Updater} class,
     * which ensures that only meaningful values (non-null, non-blank strings) are applied
     * from the update source to the existing entity.</p>
     *
     * @param id                the ID of the company to update
     * @param updatedCompanyDto the DTO containing updated values (nullable fields are ignored)
     * @return the updated CompanyDto reflecting changes in the database
     * @throws CompanyNotFound          if no company exists with the given ID
     * @throws IllegalArgumentException if updatedCompanyDto is null
     * @see Updater(Company, Company)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto update(Integer id, NewCompanyDto updatedCompanyDto) {
        Company existingCompany = storage.findById(id)
                .orElseThrow(() -> new CompanyNotFound(id));
        Company updateSource = newCompanyMapper.fromDto(updatedCompanyDto);

        Company updatedCompany = updater.update(existingCompany, updateSource);
        return companyMapper.toDto(updatedCompany);
    }

    /**
     * Deletes a company by its ID.
     * Uses a custom repository method to avoid loading the entity first.
     * <p>The operation is wrapped in a transaction to ensure atomicity.
     * First checks if the company exists; if not, throws an exception.
     * Otherwise, performs deletion using a direct delete-by-ID method.</p>
     *
     * @param companyId the ID of the company to delete
     * @throws CompanyNotFound if no company exists with the given ID
     * @see CompanyRepository#existsById(Object)
     * @see CompanyRepository#deleteCompanyById(Integer)
     */
    @Override
    @Transactional
    public void delete(Integer companyId) {
        if (!storage.existsById(companyId)) {
            throw new CompanyNotFound(companyId);
        }
        storage.deleteCompanyById(companyId);
    }

    /**
     * Retrieves a company by its ID.
     * <p>Finds the company in the database and converts it to a DTO for external use.
     * Throws an exception if the company is not found.</p>
     *
     * @param companyId the ID of the company to retrieve
     * @return the corresponding CompanyDto with all available company data
     * @throws CompanyNotFound if no company exists with the given ID
     * @see CompanyRepository#findById(Object)
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public CompanyDto findById(Integer companyId) {
        Company company = storage.findById(companyId)
                .orElseThrow(() -> new CompanyNotFound(companyId));
        return companyMapper.toDto(company);
    }

    /**
     * Retrieves all companies from the database.
     * <p>Fetches all company records, maps each entity to a DTO, and returns them as a list.
     * Returns an empty list if no companies exist — never returns null.</p>
     *
     * @return a list of CompanyDto objects; never null (may be empty)
     * @see CompanyRepository#findAll()
     * @see CompanyMapper#toDto(Company)
     */
    @Override
    public List<CompanyDto> findAll() {
        return storage.findAll().stream()
                .map(companyMapper::toDto)
                .toList();
    }
}