//package ru.avg.server.controller.web.meeting;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import ru.avg.server.model.dto.meeting.MeetingDto;
//import ru.avg.server.model.dto.meeting.NewMeetingDto;
//import ru.avg.server.service.meeting.MeetingService;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class MeetingControllerTest {
//
//    @Mock
//    private MeetingService meetingService;
//
//    @InjectMocks
//    private MeetingController meetingController;
//
//    private MeetingDto meetingDto;
//    private final Integer companyId = 1;
//    private final Integer meetingId = 101;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        meetingDto = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address("123 Main St")
//                .chairmanId(201)
//                .secretaryId(202)
//                .build();
//    }
//
//    // === GET /approval/{companyId}/meeting ===
//
//    @Test
//    void findAll_ShouldReturnOkStatusAndListOfMeetings() {
//        // Given
//        List<MeetingDto> meetings = Collections.singletonList(meetingDto);
//        when(meetingService.findAll(companyId)).thenReturn(meetings);
//
//        // When
//        ResponseEntity<List<MeetingDto>> response = meetingController.findAll(companyId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertEquals(meetingId, response.getBody().getFirst().getId());
//        assertEquals("Общее собрание акционеров", response.getBody().getFirst().getType());
//        verify(meetingService, times(1)).findAll(companyId);
//    }
//
//    @Test
//    void findAll_ShouldReturnEmptyListWhenNoMeetingsExist() {
//        // Given
//        when(meetingService.findAll(companyId)).thenReturn(Collections.emptyList());
//
//        // When
//        ResponseEntity<List<MeetingDto>> response = meetingController.findAll(companyId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody().isEmpty());
//        verify(meetingService, times(1)).findAll(companyId);
//    }
//
//    // === GET /approval/{companyId}/meeting/{meetingId} ===
//
//    @Test
//    void findById_ShouldReturnOkStatusAndMeeting() {
//        // Given
//        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingDto);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(meetingId, response.getBody().getId());
//        assertEquals("123 Main St", response.getBody().getAddress());
//        verify(meetingService, times(1)).findById(companyId, meetingId);
//    }
//
//    @Test
//    void findById_ShouldReturnMeetingWithNullChairmanAndSecretary() {
//        // Given
//        MeetingDto meetingWithoutRoles = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address("123 Main St")
//                .chairmanId(null)
//                .secretaryId(null)
//                .build();
//        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingWithoutRoles);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getChairmanId());
//        assertNull(response.getBody().getSecretaryId());
//        verify(meetingService, times(1)).findById(companyId, meetingId);
//    }
//
//    @Test
//    void findById_ShouldReturnMeetingWithNullOptionalFields() {
//        // Given
//        MeetingDto meetingWithNulls = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address(null)
//                .chairmanId(201)
//                .secretaryId(202)
//                .build();
//        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingWithNulls);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.findById(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getAddress());
//        verify(meetingService, times(1)).findById(companyId, meetingId);
//    }
//
//    // === POST /approval/{companyId}/meeting ===
//
//    @Test
//    void save_ShouldReturnCreatedStatusAndSavedMeeting() {
//        // Given
//        NewMeetingDto newMeetingDto = NewMeetingDto.builder()
//                .companyId(companyId)
//                .type("New Meeting")
//                .date(LocalDate.now())
//                .build();
//        when(meetingService.save(eq(companyId), any(NewMeetingDto.class))).thenReturn(meetingDto);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.save(companyId, newMeetingDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(meetingId, response.getBody().getId());
//        assertEquals("Общее собрание акционеров", response.getBody().getType());
//        verify(meetingService, times(1)).save(eq(companyId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void save_ShouldPreserveAllFieldsInSavedMeeting() {
//        // Given
//        NewMeetingDto newMeetingDto = NewMeetingDto.builder()
//                .companyId(companyId)
//                .type("New Meeting")
//                .date(LocalDate.now())
//                .build();
//        when(meetingService.save(eq(companyId), any(NewMeetingDto.class))).thenReturn(meetingDto);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.save(companyId, newMeetingDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(meetingDto.getId(), response.getBody().getId());
//        assertEquals(meetingDto.getCompanyId(), response.getBody().getCompanyId());
//        assertEquals(meetingDto.getAddress(), response.getBody().getAddress());
//        assertEquals(meetingDto.getChairmanId(), response.getBody().getChairmanId());
//        verify(meetingService, times(1)).save(eq(companyId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void save_ShouldHandleMeetingDtoWithNullOptionalFields() {
//        // Given
//        NewMeetingDto incompleteDto = NewMeetingDto.builder()
//                .companyId(companyId)
//                .type("Annual Meeting")
//                .date(LocalDate.now())
//                .address(null)
//                .chairmanId(null)
//                .secretaryId(null)
//                .build();
//        MeetingDto savedDto = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Annual Meeting")
//                .date(LocalDate.now())
//                .address(null)
//                .chairmanId(null)
//                .secretaryId(null)
//                .build();
//        when(meetingService.save(eq(companyId), any(NewMeetingDto.class))).thenReturn(savedDto);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.save(companyId, incompleteDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(companyId, response.getBody().getCompanyId());
//        assertNull(response.getBody().getAddress());
//        assertNull(response.getBody().getChairmanId());
//        assertNull(response.getBody().getSecretaryId());
//        verify(meetingService, times(1)).save(eq(companyId), any(NewMeetingDto.class));
//    }
//
//    // === PATCH /approval/{companyId}/meeting/{meetingId} ===
//
//    @Test
//    void update_ShouldReturnOkStatusAndUpdatedMeeting() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder()
//                .type("Extraordinary Meeting")
//                .address("456 Oak Ave")
//                .build();
//        MeetingDto updatedMeeting = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Extraordinary Meeting")
//                .date(LocalDate.now())
//                .address("456 Oak Ave")
//                .chairmanId(201)
//                .secretaryId(202)
//                .build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(updatedMeeting);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Extraordinary Meeting", response.getBody().getType());
//        assertEquals("456 Oak Ave", response.getBody().getAddress());
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void update_ShouldAllowPartialUpdateWithOnlySomeFields() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder()
//                .address("Updated Address")
//                .build();
//        MeetingDto updatedMeeting = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address("Updated Address")
//                .chairmanId(201)
//                .secretaryId(202)
//                .build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(updatedMeeting);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Updated Address", response.getBody().getAddress());
//        assertEquals("Общее собрание акционеров", response.getBody().getType()); // Unchanged
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void update_ShouldReturnMeetingWithNullValues() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder()
//                .chairmanId(null)
//                .secretaryId(null)
//                .build();
//        MeetingDto updatedMeeting = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address("123 Main St")
//                .chairmanId(null)
//                .secretaryId(null)
//                .build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(updatedMeeting);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getChairmanId());
//        assertNull(response.getBody().getSecretaryId());
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void update_ShouldHandleMeetingDtoWithNullOptionalFields() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder()
//                .address(null)
//                .chairmanId(null)
//                .build();
//        MeetingDto updatedMeeting = MeetingDto.builder()
//                .id(meetingId)
//                .companyId(companyId)
//                .type("Общее собрание акционеров")
//                .date(LocalDate.now())
//                .address(null)
//                .chairmanId(null)
//                .secretaryId(202)
//                .build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(updatedMeeting);
//
//        // When
//        ResponseEntity<MeetingDto> response = meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getAddress());
//        assertNull(response.getBody().getChairmanId());
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    // === DELETE /approval/{companyId}/meeting/{meetingId} ===
//
//    @Test
//    void remove_ShouldReturnNoContentStatus() {
//        // Given
//        doNothing().when(meetingService).delete(companyId, meetingId);
//
//        // When
//        ResponseEntity<Void> response = meetingController.remove(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(meetingService, times(1)).delete(companyId, meetingId);
//    }
//
//    @Test
//    void remove_ShouldHandleNonExistentMeetingIdGracefully() {
//        // Given
//        doThrow(new RuntimeException("Meeting not found")).when(meetingService).delete(companyId, 999);
//
//        // When/Then
//        assertThrows(RuntimeException.class, () -> meetingController.remove(companyId, 999));
//        verify(meetingService, times(1)).delete(companyId, 999);
//    }
//
//    // === Logging & Service Interaction Tests ===
//
//    @Test
//    void controllerShouldLogSaveOperation() {
//        // Given
//        NewMeetingDto newMeetingDto = NewMeetingDto.builder()
//                .companyId(companyId)
//                .type("Log Test")
//                .build();
//        when(meetingService.save(eq(companyId), any(NewMeetingDto.class))).thenReturn(meetingDto);
//
//        // When
//        meetingController.save(companyId, newMeetingDto);
//
//        // Then
//        verify(meetingService, times(1)).save(eq(companyId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void controllerShouldLogUpdateOperation() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder().type("Updated").build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(meetingDto);
//
//        // When
//        meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void controllerShouldLogDeleteOperation() {
//        // Given
//        doNothing().when(meetingService).delete(companyId, meetingId);
//
//        // When
//        meetingController.remove(companyId, meetingId);
//
//        // Then
//        verify(meetingService, times(1)).delete(companyId, meetingId);
//    }
//
//    // === Parameter Passing Verification ===
//
//    @Test
//    void findAll_ShouldCallServiceWithCompanyId() {
//        // Given
//        when(meetingService.findAll(companyId)).thenReturn(Collections.emptyList());
//
//        // When
//        meetingController.findAll(companyId);
//
//        // Then
//        verify(meetingService, times(1)).findAll(companyId);
//    }
//
//    @Test
//    void findById_ShouldCallServiceWithCompanyIdAndMeetingId() {
//        // Given
//        when(meetingService.findById(companyId, meetingId)).thenReturn(meetingDto);
//
//        // When
//        meetingController.findById(companyId, meetingId);
//
//        // Then
//        verify(meetingService, times(1)).findById(companyId, meetingId);
//    }
//
//    @Test
//    void update_ShouldPassAllParametersToService() {
//        // Given
//        NewMeetingDto updateDto = NewMeetingDto.builder().type("Updated").build();
//        when(meetingService.update(eq(companyId), eq(meetingId), any(NewMeetingDto.class))).thenReturn(meetingDto);
//
//        // When
//        meetingController.update(companyId, meetingId, updateDto);
//
//        // Then
//        verify(meetingService, times(1)).update(eq(companyId), eq(meetingId), any(NewMeetingDto.class));
//    }
//
//    @Test
//    void remove_ShouldPassCompanyIdAndMeetingIdToService() {
//        // Given
//        doNothing().when(meetingService).delete(companyId, meetingId);
//
//        // When
//        meetingController.remove(companyId, meetingId);
//
//        // Then
//        verify(meetingService, times(1)).delete(companyId, meetingId);
//    }
//}