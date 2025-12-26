package ru.avg.server.controller.web.company;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.CompanyDto;
import ru.avg.server.service.company.CompanyService;

import java.util.List;

/**
 * REST controller for managing company resources.
 * Provides CRUD operations with proper HTTP semantics and logging.
 */
@RestController
@RequestMapping("/approval/company")
@AllArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Creates a new company.
     *
     * @param companyDto the company data transfer object
     * @return ResponseEntity with CREATED status and the saved CompanyDto
     */
    @PostMapping
    public ResponseEntity<CompanyDto> save(@RequestBody @Valid CompanyDto companyDto) {
        log.debug("Saving new Company: {}", companyDto);
        CompanyDto savedCompany = companyService.save(companyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    /**
     * Retrieves a company by its ID.
     *
     * @param companyId the ID of the company
     * @return ResponseEntity with OK status and the CompanyDto
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> findById(@PathVariable Integer companyId) {
        log.debug("Finding company by companyId: {}", companyId);
        CompanyDto companyDto = companyService.findById(companyId);
        return ResponseEntity.ok(companyDto);
    }

    /**
     * Retrieves all companies.
     *
     * @return ResponseEntity with OK status and list of CompanyDto
     */
    @GetMapping
    public ResponseEntity<List<CompanyDto>> findAll() {
        log.debug("Finding all companies");
        List<CompanyDto> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    /**
     * Partially updates an existing company.
     *
     * @param companyId the ID of the company to update
     * @param company   the updated fields of the company
     * @return ResponseEntity with OK status and the updated CompanyDto
     */
    @PatchMapping("/{companyId}")
    public ResponseEntity<CompanyDto> update(
            @PathVariable Integer companyId,
            @Valid @RequestBody CompanyDto company) {
        log.debug("Editing company by companyId: {}", companyId);
        CompanyDto updatedCompany = companyService.update(companyId, company);
        return ResponseEntity.ok(updatedCompany);
    }

    /**
     * Deletes a company by its ID.
     *
     * @param companyId the ID of the company to delete
     * @return ResponseEntity with NO_CONTENT status
     */
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> delete(@PathVariable Integer companyId) {
        log.debug("Deleting company by companyId: {}", companyId);
        companyService.delete(companyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}