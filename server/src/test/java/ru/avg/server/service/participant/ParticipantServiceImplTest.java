//package ru.avg.server.service.participant;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.avg.server.exception.participant.ParticipantNotFound;
//import ru.avg.server.model.dto.participant.NewParticipantDto;
//import ru.avg.server.model.dto.participant.ParticipantDto;
//import ru.avg.server.model.dto.participant.mapper.NewParticipantMapper;
//import ru.avg.server.model.dto.participant.mapper.ParticipantMapper;
//import ru.avg.server.model.meeting.MeetingType;
//import ru.avg.server.model.participant.Participant;
//import ru.avg.server.model.participant.ParticipantType;
//import ru.avg.server.repository.participant.ParticipantRepository;
//import ru.avg.server.service.participant.impl.ParticipantServiceImpl;
//import ru.avg.server.utils.updater.Updater;
//import ru.avg.server.utils.verifier.Verifier;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ParticipantServiceImplTest {
//
//    @Mock
//    private ParticipantRepository participantRepository;
//
//    @Mock
//    private ParticipantMapper participantMapper;
//
//    @Mock
//    private NewParticipantMapper newParticipantMapper;
//
//    @Mock
//    private Verifier verifier;
//
//    @Mock
//    private Updater updater;
//
//    @InjectMocks
//    private ParticipantServiceImpl participantService;
//
//    private final Integer companyId = 1;
//    private final Integer participantId = 101;
//
//    private Participant participant;
//    private NewParticipantDto newParticipantDto;
//    private ParticipantDto participantDto;
//
//    @BeforeEach
//    void setUp() {
//        participant = Participant.builder()
//                .id(participantId)
//                .name("John Doe")
//                .share(25.5)
//                .company(null)
//                .type(ParticipantType.OWNER)
//                .isActive(true)
//                .build();
//
//        newParticipantDto = NewParticipantDto.builder()
//                .name("John Doe")
//                .share(25.5)
//                .companyId(companyId)
//                .type("Собственник")
//                .isActive(true)
//                .build();
//
//        participantDto = ParticipantDto.builder()
//                .id(participantId)
//                .name("John Doe")
//                .share(25.5)
//                .companyId(companyId)
//                .type("Собственник")
//                .isActive(true)
//                .build();
//    }
//
//    // === save() tests ===
//
//    @Test
//    void save_ShouldReturnSavedParticipant_WhenValidDataProvided() {
//        // Given
//        Participant newParticipant = Participant.builder()
//                .name("John Doe")
//                .share(25.5)
//                .type(ParticipantType.OWNER)
//                .isActive(true)
//                .build();
//
//        when(newParticipantMapper.fromDto((newParticipantDto))).thenReturn(newParticipant);
//        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
//        when(participantMapper.toDto((participant))).thenReturn(participantDto);
//
//        // When
//        ParticipantDto result = participantService.save(companyId, newParticipantDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(participantDto, result);
//        verify(verifier, times(1)).verifyCompanyAndMeeting(eq(companyId), isNull());
//        verify(newParticipantMapper, times(1)).fromDto((newParticipantDto));
//        verify(participantRepository, times(1)).save(any(Participant.class));
//        verify(participantMapper, times(1)).toDto((participant));
//    }
//
//    // === update() tests ===
//
//    @Test
//    void update_ShouldUpdateParticipant_WhenNoTypeChange() {
//        // Given
//        NewParticipantDto updatedDto = NewParticipantDto.builder()
//                .name("John Smith")
//                .share(30.0)
//                .companyId(companyId)
//                .type("Собственник")
//                .isActive(true)
//                .build();
//
//        Participant updatedParticipant = Participant.builder()
//                .name("John Smith")
//                .share(30.0)
//                .type(ParticipantType.OWNER)
//                .isActive(true)
//                .build();
//
//        when(participantRepository.findById((participantId))).thenReturn(Optional.of(participant));
//        when(newParticipantMapper.fromDto((updatedDto))).thenReturn(updatedParticipant);
//        when(updater.update((participant), (updatedParticipant))).thenReturn(updatedParticipant);
//        when(participantRepository.save((updatedParticipant))).thenReturn(updatedParticipant);
//        when(participantMapper.toDto((updatedParticipant))).thenReturn(participantDto);
//
//        // When
//        ParticipantDto result = participantService.update(companyId, participantId, updatedDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(participantDto, result);
//        verify(updater, times(1)).update((participant), (updatedParticipant));
//        verify(participantRepository, times(1)).save((updatedParticipant));
//    }
//
//    @Test
//    void update_ShouldThrowParticipantNotFound_WhenParticipantDoesNotExist() {
//        // Given
//        when(participantRepository.findById((participantId))).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(ParticipantNotFound.class, () ->
//                participantService.update(companyId, participantId, newParticipantDto));
//    }
//
//    // === find() tests ===
//
//    @Test
//    void find_ShouldReturnParticipant_WhenFound() {
//        // Given
//        when(participantRepository.findByNameAndCompanyIdAndType(
//                ("John Doe"), (companyId), (ParticipantType.OWNER)))
//                .thenReturn(Optional.of(participant));
//        when(participantMapper.toDto((participant))).thenReturn(participantDto);
//
//        // When
//        ParticipantDto result = participantService.find("John Doe", companyId, ParticipantType.OWNER);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(participantDto, result);
//    }
//
//    @Test
//    void find_ShouldThrowParticipantNotFound_WhenNotExists() {
//        // Given
//        when(participantRepository.findByNameAndCompanyIdAndType(
//                ("John Doe"), (companyId), (ParticipantType.OWNER)))
//                .thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(ParticipantNotFound.class, () ->
//                participantService.find("John Doe", companyId, ParticipantType.OWNER));
//    }
//
//    // === findAllByMeetingType() tests ===
//
//    @Test
//    void findAllByMeetingType_ShouldReturnFilteredParticipants_ForFMSAndFMP() {
//        // Given
//        Participant owner = Participant.builder().name("Owner").type(ParticipantType.OWNER).build();
//        Participant boardMember = Participant.builder().name("Board").type(ParticipantType.MEMBER_OF_BOARD).build();
//        List<Participant> allParticipants = Arrays.asList(owner, boardMember);
//
//        ParticipantDto ownerDto = ParticipantDto.builder().name("Owner").type("Собственник").build();
//
//        when(participantRepository.findAllByCompanyIdOrderByName((companyId))).thenReturn(allParticipants);
//        when(participantMapper.toDto((owner))).thenReturn(ownerDto);
//
//        // When
//        List<ParticipantDto> result = participantService.findAllByMeetingType(companyId, MeetingType.FMS);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertTrue(result.contains(ownerDto));
//    }
//
//    @Test
//    void findAllByMeetingType_ShouldReturnFilteredParticipants_ForBOD() {
//        // Given
//        Participant owner = Participant.builder().name("Owner").type(ParticipantType.OWNER).build();
//        Participant boardMember = Participant.builder().name("Board").type(ParticipantType.MEMBER_OF_BOARD).build();
//        List<Participant> allParticipants = Arrays.asList(owner, boardMember);
//
//        ParticipantDto boardMemberDto = ParticipantDto.builder().name("Board").type("Член совета директоров").build();
//
//        when(participantRepository.findAllByCompanyIdOrderByName((companyId))).thenReturn(allParticipants);
//        when(participantMapper.toDto((boardMember))).thenReturn(boardMemberDto);
//
//        // When
//        List<ParticipantDto> result = participantService.findAllByMeetingType(companyId, MeetingType.BOD);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertTrue(result.contains(boardMemberDto));
//    }
//
//    @Test
//    void findAllByMeetingType_ShouldReturnEmptyList_WhenNoParticipants() {
//        // Given
//        when(participantRepository.findAllByCompanyIdOrderByName((companyId))).thenReturn(Collections.emptyList());
//
//        // When
//        List<ParticipantDto> result = participantService.findAllByMeetingType(companyId, MeetingType.FMS);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    // === findAll() tests ===
//
//    @Test
//    void findAll_ShouldReturnAllParticipants() {
//        // Given
//        Participant p1 = Participant.builder().id(1).name("P1").build();
//        Participant p2 = Participant.builder().id(2).name("P2").build();
//        List<Participant> participants = Arrays.asList(p1, p2);
//
//        ParticipantDto p1Dto = ParticipantDto.builder().id(1).name("P1").build();
//        ParticipantDto p2Dto = ParticipantDto.builder().id(2).name("P2").build();
//
//        when(participantRepository.findAllByCompanyIdOrderByName((companyId))).thenReturn(participants);
//        when(participantMapper.toDto((p1))).thenReturn(p1Dto);
//        when(participantMapper.toDto((p2))).thenReturn(p2Dto);
//
//        // When
//        List<ParticipantDto> result = participantService.findAll(companyId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertTrue(result.containsAll(Arrays.asList(p1Dto, p2Dto)));
//    }
//
//    @Test
//    void findAll_ShouldReturnEmptyList_WhenNoParticipantsExist() {
//        // Given
//        when(participantRepository.findAllByCompanyIdOrderByName((companyId))).thenReturn(Collections.emptyList());
//
//        // When
//        List<ParticipantDto> result = participantService.findAll(companyId);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    // === findById() tests ===
//
//    @Test
//    void findById_ShouldReturnParticipant_WhenFound() {
//        // Given
//        when(participantRepository.findById((participantId))).thenReturn(Optional.of(participant));
//        when(participantMapper.toDto((participant))).thenReturn(participantDto);
//
//        // When
//        ParticipantDto result = participantService.findById(companyId, participantId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(participantDto, result);
//    }
//
//    @Test
//    void findById_ShouldThrowParticipantNotFound_WhenNotExists() {
//        // Given
//        when(participantRepository.findById((participantId))).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(ParticipantNotFound.class, () ->
//                participantService.findById(companyId, participantId));
//    }
//}