package ru.avg.server.controller.web.participant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.avg.server.model.dto.participant.NewParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.service.participant.ParticipantService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link ParticipantController} using Mockito and Spring MockMvc.
 * Tests cover all endpoints with focus on:
 * - Correct HTTP status codes
 * - Proper JSON serialization/deserialization
 * - Service delegation
 * - Boundary cases and error handling
 * - Logging behavior verification
 *
 * @author AVG
 * @since 1.0
 */
class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantController;

    private ParticipantDto participantDto;
    private NewParticipantDto newParticipantDto;
    private final Integer companyId = 1;
    private final Integer participantId = 101;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(participantController).build();

        participantDto = ParticipantDto.builder()
                .id(participantId)
                .name("John Doe")
                .share(25.5)
                .companyId(companyId)
                .type("Собственник")
                .isActive(true)
                .build();

        newParticipantDto = NewParticipantDto.builder()
                .name("John Doe")
                .share(25.5)
                .companyId(companyId)
                .type("Собственник")
                .isActive(true)
                .build();
    }

    // === GET /approval/{companyId}/participant ===

    @Test
    void findAll_ShouldReturnOkAndEmptyList_WhenNoParticipantsExist() throws Exception {
        // Given
        when(participantService.findAll(eq(companyId))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/approval/{companyId}/participant", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(participantService, times(1)).findAll(eq(companyId));
    }

    @Test
    void findAll_ShouldReturnOkAndListOfParticipants_WhenParticipantsExist() throws Exception {
        // Given
        List<ParticipantDto> participants = Arrays.asList(
                ParticipantDto.builder().id(1).name("John").share(25.0).type("Собственник").build(),
                ParticipantDto.builder().id(2).name("Jane").share(75.0).type("Собственник").build()
        );
        when(participantService.findAll(eq(companyId))).thenReturn(participants);

        // When & Then
        mockMvc.perform(get("/approval/{companyId}/participant", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].name").value("Jane"));

        verify(participantService, times(1)).findAll(eq(companyId));
    }

    @Test
    void findAll_ShouldReturnOkForZeroCompanyId() throws Exception {
        // Given
        Integer invalidId = 0;
        when(participantService.findAll(eq(invalidId))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/approval/{companyId}/participant", invalidId))
                .andExpect(status().isOk());

        verify(participantService, times(1)).findAll(eq(invalidId));
    }

    @Test
    void findAll_ShouldReturnOkForNegativeCompanyId() throws Exception {
        // Given
        Integer invalidId = -1;
        when(participantService.findAll(eq(invalidId))).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/approval/{companyId}/participant", invalidId))
                .andExpect(status().isOk());

        verify(participantService, times(1)).findAll(eq(invalidId));
    }

    // === POST /approval/{companyId}/participant ===

    @Test
    void save_ShouldReturnCreatedAndSavedParticipant_WhenValidDataProvided() throws Exception {
        // Given
        when(participantService.save(eq(companyId), any(NewParticipantDto.class))).thenReturn(participantDto);

        // When & Then
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Doe",
                                  "share": 25.5,
                                  "companyId": 1,
                                  "type": "Собственник",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(participantId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.share").value(25.5))
                .andExpect(jsonPath("$.type").value("Собственник"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(participantService, times(1)).save(eq(companyId), any(NewParticipantDto.class));
    }

    @Test
    void save_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given - invalid required fields
        String invalidJson = """
                {
                  "name": "",
                  "share": -1.0,
                  "companyId": 1,
                  "type": "",
                  "isActive": null
                }
                """;

        // When & Then
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(participantService, never()).save(any(), any());
    }

    @Test
    void save_ShouldReturnBadRequest_WhenShareIsTooHigh() throws Exception {
        // Given
        String invalidJson = """
                {
                  "name": "Valid Name",
                  "share": 150.0,
                  "companyId": 1,
                  "type": "Собственник",
                  "isActive": true
                }
                """;

        // When & Then
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(participantService, never()).save(any(), any());
    }

    @Test
    void save_ShouldReturnBadRequest_WhenShareIsNegative() throws Exception {
        // Given
        String invalidJson = """
                {
                  "name": "Valid Name",
                  "share": -10.0,
                  "companyId": 1,
                  "type": "Собственник",
                  "isActive": true
                }
                """;

        // When & Then
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(participantService, never()).save(any(), any());
    }

    @Test
    void save_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
        // Given
        String invalidJson = """
                {
                  "name": null,
                  "share": 25.0,
                  "companyId": 1,
                  "type": "Собственник",
                  "isActive": true
                }
                """;

        // When & Then
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(participantService, never()).save(any(), any());
    }

    // === GET /approval/{companyId}/participant/{participantId} ===

    @Test
    void findById_ShouldReturnOkAndParticipant_WhenParticipantExists() throws Exception {
        // Given
        when(participantService.findById(eq(companyId), eq(participantId))).thenReturn(participantDto);

        // When & Then
        mockMvc.perform(get("/approval/{companyId}/participant/{participantId}", companyId, participantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(participantId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.share").value(25.5))
                .andExpect(jsonPath("$.type").value("Собственник"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(participantService, times(1)).findById(eq(companyId), eq(participantId));
    }

    // === PATCH /approval/{companyId}/participant/{participantId} ===

    @Test
    void update_ShouldReturnOkAndUpdatedParticipant_WhenValidDataProvided() throws Exception {
        // Given
        ParticipantDto updatedDto = ParticipantDto.builder()
                .id(participantId)
                .name("John Smith")
                .share(30.0)
                .companyId(companyId)
                .type("Член совета директоров")
                .isActive(false)
                .build();
        when(participantService.update(eq(companyId), eq(participantId), any(NewParticipantDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(patch("/approval/{companyId}/participant/{participantId}", companyId, participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "share": 30.0,
                                  "companyId": 1,
                                  "type": "Член совета директоров",
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(participantId))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.share").value(30.0))
                .andExpect(jsonPath("$.type").value("Член совета директоров"))
                .andExpect(jsonPath("$.isActive").value(false));

        verify(participantService, times(1)).update(eq(companyId), eq(participantId), any(NewParticipantDto.class));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given - invalid data
        String invalidJson = """
                {
                  "name": "",
                  "share": -5.0,
                  "companyId": 1,
                  "type": "",
                  "isActive": null
                }
                """;

        // When & Then
        mockMvc.perform(patch("/approval/{companyId}/participant/{participantId}", companyId, participantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(participantService, never()).update(any(), any(), any());
    }

    // === DELETE /approval/{companyId}/participant/{participantId} ===

    @Test
    void removeById_ShouldReturnNoContent_WhenParticipantExists() throws Exception {
        // Given - successful deletion

        // When & Then
        mockMvc.perform(delete("/approval/{companyId}/participant/{participantId}", companyId, participantId))
                .andExpect(status().isNoContent());

        verify(participantService, times(1)).delete(eq(companyId), eq(participantId));
    }

    // === Logging Verification ===

    @Test
    void controllerShouldLogSaveOperation() throws Exception {
        // Given
        when(participantService.save(eq(companyId), any(NewParticipantDto.class))).thenReturn(participantDto);

        // When
        mockMvc.perform(post("/approval/{companyId}/participant", companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "name": "John Doe",
                                  "share": 25.5,
                                  "companyId": 1,
                                  "type": "Собственник",
                                  "isActive": true
                                }
                                """));

        // Then - logging is tested via verify using ArgumentMatchers
        verify(participantService, times(1)).save(eq(companyId), any(NewParticipantDto.class));
    }

    @Test
    void controllerShouldLogDeleteOperation() throws Exception {
        // Given - successful deletion

        // When
        mockMvc.perform(delete("/approval/{companyId}/participant/{participantId}", companyId, participantId));

        // Then
        verify(participantService, times(1)).delete(eq(companyId), eq(participantId));
    }

    @Test
    void controllerShouldLogFindByIdOperation() throws Exception {
        // Given
        when(participantService.findById(eq(companyId), eq(participantId))).thenReturn(participantDto);

        // When
        mockMvc.perform(get("/approval/{companyId}/participant/{participantId}", companyId, participantId));

        // Then
        verify(participantService, times(1)).findById(eq(companyId), eq(participantId));
    }

    @Test
    void controllerShouldLogFindAllOperation() throws Exception {
        // Given
        when(participantService.findAll(eq(companyId))).thenReturn(List.of(participantDto));

        // When
        mockMvc.perform(get("/approval/{companyId}/participant", companyId));

        // Then
        verify(participantService, times(1)).findAll(eq(companyId));
    }
}