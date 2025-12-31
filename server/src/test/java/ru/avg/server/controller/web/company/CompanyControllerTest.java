package ru.avg.server.controller.web.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.avg.server.model.dto.CompanyDto;
import ru.avg.server.service.company.CompanyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private CompanyDto companyDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        companyDto = CompanyDto.builder()
                .id(1)
                .title("Test Company")
                .inn(1234567890L)
                .companyType("AO")
                .hasBoardOfDirectors(true)
                .build();
    }

    @Test
    void save_ShouldReturnCreatedStatusAndSavedCompany() {
        // Given
        when(companyService.save(any(CompanyDto.class))).thenReturn(companyDto);

        // When
        ResponseEntity<CompanyDto> response = companyController.save(companyDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(companyDto.getId(), response.getBody().getId());
        assertEquals(companyDto.getTitle(), response.getBody().getTitle());
        verify(companyService, times(1)).save(companyDto);
    }

    @Test
    void findById_ShouldReturnOkStatusAndCompany_WhenCompanyExists() {
        // Given
        Integer companyId = 1;
        when(companyService.findById(companyId)).thenReturn(companyDto);

        // When
        ResponseEntity<CompanyDto> response = companyController.findById(companyId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(companyDto.getId(), response.getBody().getId());
        verify(companyService, times(1)).findById(companyId);
    }

    @Test
    void findById_ShouldReturnCompanyEvenIfOptionalFieldsAreNull() {
        // Given
        CompanyDto companyWithNulls = CompanyDto.builder()
                .id(2)
                .title("Null Test Company")
                .inn(9876543210L)
                .companyType("OOO")
                .hasBoardOfDirectors(null)
                .build();
        when(companyService.findById(2)).thenReturn(companyWithNulls);

        // When
        ResponseEntity<CompanyDto> response = companyController.findById(2);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getHasBoardOfDirectors());
        verify(companyService, times(1)).findById(2);
    }

    @Test
    void findAll_ShouldReturnOkStatusAndListOfCompanies() {
        // Given
        List<CompanyDto> companies = List.of(companyDto);
        when(companyService.findAll()).thenReturn(companies);

        // When
        ResponseEntity<List<CompanyDto>> response = companyController.findAll();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Company", response.getBody().getFirst().getTitle());
        verify(companyService, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoCompaniesExist() {
        // Given
        when(companyService.findAll()).thenReturn(List.of());

        // When
        ResponseEntity<List<CompanyDto>> response = companyController.findAll();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(companyService, times(1)).findAll();
    }

    @Test
    void update_ShouldReturnOkStatusAndUpdatedCompany() {
        // Given
        Integer companyId = 1;
        CompanyDto updateDto = CompanyDto.builder()
                .title("Updated Company")
                .hasBoardOfDirectors(false)
                .build();
        CompanyDto updatedCompany = CompanyDto.builder()
                .id(companyId)
                .title("Updated Company")
                .inn(1234567890L)
                .companyType("AO")
                .hasBoardOfDirectors(false)
                .build();
        when(companyService.update(eq(companyId), any(CompanyDto.class))).thenReturn(updatedCompany);

        // When
        ResponseEntity<CompanyDto> response = companyController.update(companyId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Company", response.getBody().getTitle());
        assertFalse(response.getBody().getHasBoardOfDirectors());
        assertEquals(1234567890L, response.getBody().getInn());
        verify(companyService, times(1)).update(eq(companyId), any(CompanyDto.class));
    }

    @Test
    void update_ShouldAllowPartialUpdateWithNullValues() {
        // Given
        Integer companyId = 1;
        CompanyDto updateDto = CompanyDto.builder()
                .hasBoardOfDirectors(null)
                .build();
        CompanyDto updatedCompany = CompanyDto.builder()
                .id(companyId)
                .title("Test Company")
                .inn(1234567890L)
                .companyType("AO")
                .hasBoardOfDirectors(null)
                .build();
        when(companyService.update(eq(companyId), any(CompanyDto.class))).thenReturn(updatedCompany);

        // When
        ResponseEntity<CompanyDto> response = companyController.update(companyId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getHasBoardOfDirectors());
        verify(companyService, times(1)).update(eq(companyId), any(CompanyDto.class));
    }

    @Test
    void delete_ShouldReturnNoContentStatus() {
        // Given
        Integer companyId = 1;
        doNothing().when(companyService).delete(companyId);

        // When
        ResponseEntity<Void> response = companyController.delete(companyId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(companyService, times(1)).delete(companyId);
    }

    @Test
    void delete_ShouldHandleNonExistentCompanyIdGracefully() {
        // Given
        Integer nonExistentId = 999;
        doNothing().when(companyService).delete(nonExistentId);

        // When
        ResponseEntity<Void> response = companyController.delete(nonExistentId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(companyService, times(1)).delete(nonExistentId);
    }
}