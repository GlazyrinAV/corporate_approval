//package ru.avg.server.controller.web.meeting;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import ru.avg.server.model.dto.participant.MeetingParticipantDto;
//import ru.avg.server.model.dto.participant.NewMeetingParticipantDto;
//import ru.avg.server.model.dto.participant.ParticipantDto;
//import ru.avg.server.service.participant.MeetingParticipantService;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class MeetingParticipantControllerTest {
//
//    @Mock
//    private MeetingParticipantService meetingParticipantService;
//
//    @InjectMocks
//    private MeetingParticipantController meetingParticipantController;
//
//    private MeetingParticipantDto participantDto;
//    private NewMeetingParticipantDto creationDto;
//    private final Integer companyId = 1;
//    private final Integer meetingId = 101;
//    private final Integer participantId = 201;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        participantDto = MeetingParticipantDto.builder()
//                .id(participantId)
//                .meetingId(meetingId)
//                .isPresent(true)
//                .participantId(ParticipantDto.builder()
//                        .id(301)
//                        .name("John Doe")
//                        .share(50.0)
//                        .companyId(companyId)
//                        .type("OWNER")
//                        .isActive(true)
//                        .build())
//                .build();
//        creationDto = NewMeetingParticipantDto.builder()
//                .potentialParticipants(Collections.singletonList(participantDto))
//                .build();
//    }
//
//    @Test
//    void findPotentialParticipants_ShouldReturnOkStatusAndListOfPotentialParticipants() {
//        // Given
//        List<MeetingParticipantDto> potentials = Collections.singletonList(participantDto);
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(potentials);
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertEquals(participantId, response.getBody().getFirst().getId());
//        assertEquals("John Doe", response.getBody().getFirst().getParticipantId().getName());
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void findPotentialParticipants_ShouldReturnEmptyListWhenNoPotentialParticipantsExist() {
//        // Given
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody().isEmpty());
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void findPotentialParticipants_ShouldReturnListOfMultiplePotentialParticipants() {
//        // Given
//        MeetingParticipantDto secondParticipant = MeetingParticipantDto.builder()
//                .id(202)
//                .participantId(ParticipantDto.builder()
//                        .id(302)
//                        .name("Jane Smith")
//                        .build())
//                .build();
//        List<MeetingParticipantDto> potentials = Arrays.asList(participantDto, secondParticipant);
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(potentials);
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(2, response.getBody().size());
//        assertTrue(response.getBody().stream().anyMatch(p -> "John Doe".equals(p.getParticipantId().getName())));
//        assertTrue(response.getBody().stream().anyMatch(p -> "Jane Smith".equals(p.getParticipantId().getName())));
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void findPotentialParticipants_ShouldReturnParticipantsWithNullFields() {
//        // Given
//        MeetingParticipantDto participantWithNulls = MeetingParticipantDto.builder()
//                .isPresent(null)
//                .participantId(ParticipantDto.builder()
//                        .share(null)
//                        .build())
//                .build();
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(Collections.singletonList(participantWithNulls));
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertNull(response.getBody().getFirst().getIsPresent());
//        assertNull(response.getBody().getFirst().getParticipantId().getShare());
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipants_ShouldReturnOkStatusAndListOfCurrentParticipants() {
//        // Given
//        List<MeetingParticipantDto> participants = Collections.singletonList(participantDto);
//        when(meetingParticipantService.findAll(companyId, meetingId)).thenReturn(participants);
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertEquals("John Doe", response.getBody().getFirst().getParticipantId().getName());
//        verify(meetingParticipantService, times(1)).findAll(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipants_ShouldReturnEmptyListWhenNoParticipantsExist() {
//        // Given
//        when(meetingParticipantService.findAll(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody().isEmpty());
//        verify(meetingParticipantService, times(1)).findAll(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipants_ShouldReturnMultipleParticipants() {
//        // Given
//        MeetingParticipantDto secondParticipant = MeetingParticipantDto.builder()
//                .id(202)
//                .participantId(ParticipantDto.builder()
//                        .id(302)
//                        .name("Jane Smith")
//                        .build())
//                .build();
//        List<MeetingParticipantDto> participants = Arrays.asList(participantDto, secondParticipant);
//        when(meetingParticipantService.findAll(companyId, meetingId)).thenReturn(participants);
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.findParticipants(companyId, meetingId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(2, response.getBody().size());
//        assertTrue(response.getBody().stream().anyMatch(p -> "John Doe".equals(p.getParticipantId().getName())));
//        assertTrue(response.getBody().stream().anyMatch(p -> "Jane Smith".equals(p.getParticipantId().getName())));
//        verify(meetingParticipantService, times(1)).findAll(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipant_ShouldReturnOkStatusAndParticipant() {
//        // Given
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(participantDto);
//
//        // When
//        ResponseEntity<MeetingParticipantDto> response = meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(participantId, response.getBody().getId());
//        assertEquals(50.0, response.getBody().getParticipantId().getShare());
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void findParticipant_ShouldReturnParticipantWithNullMeetingId() {
//        // Given
//        MeetingParticipantDto dtoWithNullMeeting = MeetingParticipantDto.builder()
//                .meetingId(null)
//                .build();
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(dtoWithNullMeeting);
//
//        // When
//        ResponseEntity<MeetingParticipantDto> response = meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getMeetingId());
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void findParticipant_ShouldReturnParticipantWithInactiveStatus() {
//        // Given
//        MeetingParticipantDto inactiveParticipant = MeetingParticipantDto.builder()
//                .participantId(ParticipantDto.builder()
//                        .isActive(false)
//                        .build())
//                .build();
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(inactiveParticipant);
//
//        // When
//        ResponseEntity<MeetingParticipantDto> response = meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertFalse(response.getBody().getParticipantId().getIsActive());
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void findParticipant_ShouldReturnParticipantWithNullOptionalFields() {
//        // Given
//        MeetingParticipantDto participantWithNulls = MeetingParticipantDto.builder()
//                .isPresent(null)
//                .participantId(ParticipantDto.builder()
//                        .share(null)
//                        .build())
//                .build();
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(participantWithNulls);
//
//        // When
//        ResponseEntity<MeetingParticipantDto> response = meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNull(response.getBody().getIsPresent());
//        assertNull(response.getBody().getParticipantId().getShare());
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void saveMeetingParticipant_ShouldReturnCreatedStatusAndSavedParticipants() {
//        // Given
//        List<MeetingParticipantDto> savedParticipants = Collections.singletonList(participantDto);
//        when(meetingParticipantService.save(eq(companyId), eq(meetingId), anyList())).thenReturn(savedParticipants);
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.saveMeetingParticipant(companyId, meetingId, creationDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertEquals("John Doe", response.getBody().getFirst().getParticipantId().getName());
//        verify(meetingParticipantService, times(1)).save((companyId), (meetingId), (creationDto.getPotentialParticipants()));
//    }
//
//    @Test
//    void saveMeetingParticipant_ShouldHandleEmptyPotentialParticipantsList() {
//        // Given
//        NewMeetingParticipantDto emptyDto = NewMeetingParticipantDto.builder()
//                .potentialParticipants(Collections.emptyList())
//                .build();
//        when(meetingParticipantService.save(eq(companyId), eq(meetingId), anyList())).thenReturn(Collections.emptyList());
//
//        // When
//        ResponseEntity<List<MeetingParticipantDto>> response = meetingParticipantController.saveMeetingParticipant(companyId, meetingId, emptyDto);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody().isEmpty());
//        verify(meetingParticipantService, times(1)).save((companyId), (meetingId), (Collections.emptyList()));
//    }
//
//    @Test
//    void removeMeetingParticipant_ShouldReturnNoContentStatus() {
//        // Given
//        doNothing().when(meetingParticipantService).delete(companyId, meetingId, participantId);
//
//        // When
//        ResponseEntity<Void> response = meetingParticipantController.removeMeetingParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(meetingParticipantService, times(1)).delete(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void removeMeetingParticipant_ShouldHandleNonExistentParticipantIdGracefully() {
//        // Given
//        doNothing().when(meetingParticipantService).delete(companyId, meetingId, 999);
//
//        // When
//        ResponseEntity<Void> response = meetingParticipantController.removeMeetingParticipant(companyId, meetingId, 999);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(meetingParticipantService, times(1)).delete(companyId, meetingId, 999);
//    }
//
//    @Test
//    void removeMeetingParticipant_ShouldAcceptNullCompanyId() {
//        // Given
//        final Integer nullCompanyId = null;
//        doNothing().when(meetingParticipantService).delete(nullCompanyId, meetingId, participantId);
//
//        // When
//        ResponseEntity<Void> response = meetingParticipantController.removeMeetingParticipant(nullCompanyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(meetingParticipantService, times(1)).delete(nullCompanyId, meetingId, participantId);
//    }
//
//    @Test
//    void removeMeetingParticipant_ShouldHandleMultipleCallsIdempotently() {
//        // Given
//        doNothing().when(meetingParticipantService).delete(companyId, meetingId, participantId);
//
//        // When
//        ResponseEntity<Void> response1 = meetingParticipantController.removeMeetingParticipant(companyId, meetingId, participantId);
//        ResponseEntity<Void> response2 = meetingParticipantController.removeMeetingParticipant(companyId, meetingId, participantId);
//
//        // Then
//        assertNotNull(response1);
//        assertEquals(HttpStatus.NO_CONTENT, response1.getStatusCode());
//        assertNotNull(response2);
//        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
//        verify(meetingParticipantService, times(2)).delete(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void controllerShouldLogFindPotentialOperation() {
//        // Given
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void controllerShouldLogFindParticipantsOperation() {
//        // Given
//        when(meetingParticipantService.findAll(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        meetingParticipantController.findParticipants(companyId, meetingId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findAll(companyId, meetingId);
//    }
//
//    @Test
//    void controllerShouldLogFindParticipantOperation() {
//        // Given
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(participantDto);
//
//        // When
//        meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void controllerShouldLogSaveOperation() {
//        // Given
//        when(meetingParticipantService.save(eq(companyId), eq(meetingId), anyList())).thenReturn(Collections.singletonList(participantDto));
//
//        // When
//        meetingParticipantController.saveMeetingParticipant(companyId, meetingId, creationDto);
//
//        // Then
//        verify(meetingParticipantService, times(1)).save((companyId), (meetingId), (creationDto.getPotentialParticipants()));
//    }
//
//    @Test
//    void controllerShouldLogDeleteOperation() {
//        // Given
//        doNothing().when(meetingParticipantService).delete(companyId, meetingId, participantId);
//
//        // When
//        meetingParticipantController.removeMeetingParticipant(companyId, meetingId, participantId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).delete(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void findPotentialParticipants_ShouldCallServiceWithCompanyIdAndMeetingId() {
//        // Given
//        when(meetingParticipantService.findPotential(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        meetingParticipantController.findPotentialParticipants(companyId, meetingId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findPotential(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipants_ShouldCallServiceWithCompanyIdAndMeetingId() {
//        // Given
//        when(meetingParticipantService.findAll(companyId, meetingId)).thenReturn(Collections.emptyList());
//
//        // When
//        meetingParticipantController.findParticipants(companyId, meetingId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findAll(companyId, meetingId);
//    }
//
//    @Test
//    void findParticipant_ShouldCallServiceWithAllPathVariables() {
//        // Given
//        when(meetingParticipantService.findByParticipantId(companyId, meetingId, participantId)).thenReturn(participantDto);
//
//        // When
//        meetingParticipantController.findParticipant(companyId, meetingId, participantId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).findByParticipantId(companyId, meetingId, participantId);
//    }
//
//    @Test
//    void saveMeetingParticipant_ShouldPassAllParametersToService() {
//        // Given
//        @Valid @NotNull(message = "List of potential participants must not be null") List<@Valid MeetingParticipantDto> potentialList = creationDto.getPotentialParticipants();
//        when(meetingParticipantService.save(eq(companyId), eq(meetingId), anyList())).thenReturn(Collections.singletonList(participantDto));
//
//        // When
//        meetingParticipantController.saveMeetingParticipant(companyId, meetingId, creationDto);
//
//        // Then
//        verify(meetingParticipantService, times(1)).save((companyId), (meetingId), (potentialList));
//    }
//
//    @Test
//    void removeMeetingParticipant_ShouldCallServiceWithAllPathVariables() {
//        // Given
//        doNothing().when(meetingParticipantService).delete(companyId, meetingId, participantId);
//
//        // When
//        meetingParticipantController.removeMeetingParticipant(companyId, meetingId, participantId);
//
//        // Then
//        verify(meetingParticipantService, times(1)).delete(companyId, meetingId, participantId);
//    }
//}