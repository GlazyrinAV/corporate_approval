package ru.avg.server.controller.web.company;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.avg.server.model.dto.CompanyDto;
import ru.avg.server.service.company.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/approval/company")
@AllArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public CompanyDto save(@RequestBody @Valid CompanyDto companyDto,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("Saving new Company: {}", companyDto);
        return companyService.save(companyDto);
    }

    @GetMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyDto findById(@PathVariable("companyId") Integer companyId) {
        log.info("Finding company by companyId: {}", companyId);
        return companyService.findById(companyId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyDto> findAll() {
        log.info("Finding all companies");
        return companyService.findAll();
    }

    @PostMapping("/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDto edit(@PathVariable("companyId") Integer companyId,
                           @Valid @RequestBody CompanyDto company,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.info("Validation errors: {}", result.getAllErrors());
        }
        log.info("Editing company by companyId: {}", companyId);
        return companyService.edit(companyId, company);
    }

    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") Integer companyId) {
        log.info("Deleting company by companyId: {}", companyId);
        companyService.delete(companyId);
    }
}