package ru.avg.server.controller.web.meeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.avg.server.model.dto.MeetingDto;
import ru.avg.server.service.meeting.MeetingService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingControllerTest {

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController meetingController;

    private MeetingDto meetingDto;
    private final Integer companyId = 1;
    private final Integer meetingId = 101;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meetingDto = MeetingDto.builder()
                .id(meetingId)
                .companyId(companyId)
                .type("Общее собрание акционеров")
                .date(LocalDate.now())
                .address("123 Main St")
                .chairmanId(201)
                .secretaryId(202)
                .build();
    }

    @Test
    void findAll_ShouldReturnOkStatusAndListOfMeetings() {
        // Given
        List<MeetingDto> meetings = Collections.singletonList(meetingDto);
        when(meetingService.findAll(companyId)).thenReturn(meetings);

        // When
        ResponseEntity<List<MeetingDto>> response = meetingController.findAll(companyId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(meetingId, response.getBody().getFirst().getId());
        assertEquals("Общее собрание акционеров", response.getBody().getFirst().getType());
        verify(meetingService, times(1)).findAll(companyId);
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoMeetingsExist() {
        // Given
        when(meetingService.findAll(companyId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<MeetingDto>> response = meetingController.findAll(companyId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(meetingService, times(1)).findAll(companyId);
    }

    @Test
    void findById_ShouldReturnOkStatusAndMeeting() {
        // Given
        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingDto);

        // When
        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(meetingId, response.getBody().getId());
        assertEquals("123 Main St", response.getBody().getAddress());
        verify(meetingService, times(1)).findById(companyId, meetingId);
    }

    @Test
    void findById_ShouldReturnMeetingWithNullChairmanAndSecretary() {
        // Given
        MeetingDto meetingWithoutRoles = MeetingDto.builder()
                .chairmanId(null)
                .secretaryId(null)
                .build();
        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingWithoutRoles);

        // When
        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getChairmanId());
        assertNull(response.getBody().getSecretaryId());
        verify(meetingService, times(1)).findById(companyId, meetingId);
    }

    @Test
    void findById_ShouldReturnMeetingWithNullOptionalFields() {
        // Given
        MeetingDto meetingWithNulls = MeetingDto.builder()
                .address(null)
                .build();
        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingWithNulls);

        // When
        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getAddress());
        verify(meetingService, times(1)).findById(companyId, meetingId);
    }

    @Test
    void save_ShouldReturnCreatedStatusAndSavedMeeting() {
        // Given
        MeetingDto newMeetingDto = MeetingDto.builder()
                .id(null)
                .build();
        when(meetingService.save(eq(companyId), any(MeetingDto.class))).thenReturn(meetingDto);

        // When
        ResponseEntity<MeetingDto> response = meetingController.save(companyId, newMeetingDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(meetingId, response.getBody().getId());
        assertEquals("Общее собрание акционеров", response.getBody().getType());
        verify(meetingService, times(1)).save(eq(companyId), argThat(dto -> dto.getId() == null));
    }

    @Test
    void save_ShouldPreserveAllFieldsInSavedMeeting() {
        // Given
        when(meetingService.save(eq(companyId), any(MeetingDto.class))).thenReturn(meetingDto);

        // When
        ResponseEntity<MeetingDto> response = meetingController.save(companyId, meetingDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(meetingDto.getId(), response.getBody().getId());
        assertEquals(meetingDto.getCompanyId(), response.getBody().getCompanyId());
        assertEquals(meetingDto.getAddress(), response.getBody().getAddress());
        assertEquals(meetingDto.getChairmanId(), response.getBody().getChairmanId());
        verify(meetingService, times(1)).save(eq(companyId), any(MeetingDto.class));
    }

    @Test
    void update_ShouldReturnOkStatusAndUpdatedMeeting() {
        // Given
        MeetingDto updateDto = MeetingDto.builder()
                .type("Extraordinary Meeting")
                .address("456 Oak Ave")
                .build();
        MeetingDto updatedMeeting = MeetingDto.builder()
                .type("Extraordinary Meeting")
                .address("456 Oak Ave")
                .build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(updatedMeeting);

        // When
        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Extraordinary Meeting", response.getBody().getType());
        assertEquals("456 Oak Ave", response.getBody().getAddress());
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }

    @Test
    void update_ShouldAllowPartialUpdateWithOnlySomeFields() {
        // Given
        MeetingDto updateDto = MeetingDto.builder()
                .address("Updated Address")
                .build();
        MeetingDto updatedMeeting = MeetingDto.builder()
                .id(meetingId)
                .companyId(companyId)
                .type("Общее собрание акционеров")
                .date(LocalDate.now())
                .address("Updated Address")
                .chairmanId(201)
                .secretaryId(202)
                .build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(updatedMeeting);

        // When
        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Address", response.getBody().getAddress());
        assertEquals("Общее собрание акционеров", response.getBody().getType()); // Unchanged
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }

    @Test
    void update_ShouldReturnMeetingWithNullValues() {
        // Given
        MeetingDto updateDto = MeetingDto.builder()
                .chairmanId(null)
                .secretaryId(null)
                .build();
        MeetingDto updatedMeeting = MeetingDto.builder()
                .chairmanId(null)
                .secretaryId(null)
                .build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(updatedMeeting);

        // When
        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getChairmanId());
        assertNull(response.getBody().getSecretaryId());
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }

    @Test
    void remove_ShouldReturnNoContentStatus() {
        // Given
        doNothing().when(meetingService).delete(companyId, meetingId);

        // When
        ResponseEntity<Void> response = meetingController.remove(companyId, meetingId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(meetingService, times(1)).delete(companyId, meetingId);
    }

    @Test
    void remove_ShouldHandleNonExistentMeetingIdGracefully() {
        // Given
        doNothing().when(meetingService).delete(companyId, 999);

        // When
        ResponseEntity<Void> response = meetingController.remove(companyId, 999);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(meetingService, times(1)).delete(companyId, 999);
    }

    @Test
    void controllerShouldLogSaveOperation() {
        // Given
        MeetingDto newMeetingDto = MeetingDto.builder().id(null).build();
        when(meetingService.save(eq(companyId), any(MeetingDto.class))).thenReturn(meetingDto);

        // When
        meetingController.save(companyId, newMeetingDto);

        // Then
        verify(meetingService, times(1)).save(eq(companyId), any(MeetingDto.class));
    }

    @Test
    void controllerShouldLogUpdateOperation() {
        // Given
        MeetingDto updateDto = MeetingDto.builder().type("Updated").build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(meetingDto);

        // When
        meetingController.update(companyId, meetingId, updateDto);

        // Then
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }

    @Test
    void controllerShouldLogDeleteOperation() {
        // Given
        doNothing().when(meetingService).delete(companyId, meetingId);

        // When
        meetingController.remove(companyId, meetingId);

        // Then
        verify(meetingService, times(1)).delete(companyId, meetingId);
    }

    @Test
    void findAll_ShouldCallServiceWithCompanyId() {
        // Given
        when(meetingService.findAll(companyId)).thenReturn(Collections.emptyList());

        // When
        meetingController.findAll(companyId);

        // Then
        verify(meetingService, times(1)).findAll(companyId);
    }

    @Test
    void findById_ShouldCallServiceWithCompanyIdAndMeetingId() {
        // Given
        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingDto);

        // When
        meetingController.findById(companyId, meetingId);

        // Then
        verify(meetingService, times(1)).findById(companyId, meetingId);
    }

    @Test
    void update_ShouldPassAllParametersToService() {
        // Given
        MeetingDto updateDto = MeetingDto.builder().type("Updated").build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(meetingDto);

        // When
        meetingController.update(companyId, meetingId, updateDto);

        // Then
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }

    @Test
    void remove_ShouldPassCompanyIdAndMeetingIdToService() {
        // Given
        doNothing().when(meetingService).delete(companyId, meetingId);

        // When
        meetingController.remove(companyId, meetingId);

        // Then
        verify(meetingService, times(1)).delete(companyId, meetingId);
    }

    @Test
    void save_ShouldHandleMeetingDtoWithNullOptionalFields() {
        // Given
        MeetingDto incompleteDto = MeetingDto.builder()
                .companyId(companyId)
                .type("Annual Meeting")
                .date(LocalDate.now())
                .address(null)
                .chairmanId(null)
                .secretaryId(null)
                .build();
        MeetingDto savedDto = MeetingDto.builder()
                .id(meetingId)
                .build();
        when(meetingService.save(eq(companyId), any(MeetingDto.class))).thenReturn(savedDto);

        // When
        ResponseEntity<MeetingDto> response = meetingController.save(companyId, incompleteDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getAddress());
        assertNull(response.getBody().getChairmanId());
        assertNull(response.getBody().getSecretaryId());
        verify(meetingService, times(1)).save(eq(companyId), any(MeetingDto.class));
    }

    @Test
    void update_ShouldHandleMeetingDtoWithNullOptionalFields() {
        // Given
        MeetingDto updateDto = MeetingDto.builder()
                .address(null)
                .chairmanId(null)
                .build();
        MeetingDto updatedMeeting = MeetingDto.builder()
                .address(null)
                .chairmanId(null)
                .build();
        when(meetingService.update(eq(companyId), eq(meetingId), any(MeetingDto.class))).thenReturn(updatedMeeting);

        // When
        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getAddress());
        assertNull(response.getBody().getChairmanId());
        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(MeetingDto.class));
    }
}