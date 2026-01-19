//package ru.avg.server.controller.web.topic;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.avg.server.model.dto.topic.NewTopicDto;
//import ru.avg.server.model.dto.topic.TopicDto;
//import ru.avg.server.service.topic.TopicService;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class TopicControllerTest {
//
//    @Mock
//    private TopicService topicService;
//
//    @InjectMocks
//    private TopicController topicController;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    private TopicDto topicDto;
//
//    private NewTopicDto newTopicDto;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(topicController).build();
//        objectMapper = new ObjectMapper();
//
//        topicDto = TopicDto.builder()
//                .id(1)
//                .title("Budget Review")
//                .meetingId(1)
//                .build();
//
//        newTopicDto = NewTopicDto.builder()
//                .title("Budget Review")
//                .meetingId(1)
//                .build();
//    }
//
//    // --- findAll Tests ---
//
//    @Test
//    void findAll_ShouldReturnAllTopics_WhenValidIds() throws Exception {
//        when(topicService.findAllByMeetingId(anyInt(), anyInt())).thenReturn(List.of(topicDto));
//
//        mockMvc.perform(get("/approval/1/meeting/10/topic"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].title").value("Budget Review"));
//
//        verify(topicService, times(1)).findAllByMeetingId(1, 10);
//    }
//
//    @Test
//    void findAll_ShouldReturnEmptyList_WhenNoTopicsExist() throws Exception {
//        when(topicService.findAllByMeetingId(anyInt(), anyInt())).thenReturn(List.of());
//
//        mockMvc.perform(get("/approval/1/meeting/10/topic"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//
//        verify(topicService, times(1)).findAllByMeetingId(1, 10);
//    }
//
//    // --- findById Tests ---
//
//    @Test
//    void findById_ShouldReturnTopic_WhenExists() throws Exception {
//        when(topicService.findById(anyInt(), anyInt(), anyInt())).thenReturn(topicDto);
//
//        mockMvc.perform(get("/approval/1/meeting/10/topic/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Budget Review"));
//
//        verify(topicService, times(1)).findById(1, 10, 1);
//    }
//
//    // --- save Tests ---
//
//    @Test
//    void save_ShouldCreateTopic_WhenValidData() throws Exception {
//        when(topicService.save(anyInt(), anyInt(), any(NewTopicDto.class))).thenReturn(topicDto);
//
//        mockMvc.perform(post("/approval/1/meeting/10/topic")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(topicDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Budget Review"));
//
//        verify(topicService, times(1)).save(eq(1), eq(10), any(NewTopicDto.class));
//    }
//
//    @Test
//    void save_ShouldRejectMalformedJson() throws Exception {
//        mockMvc.perform(post("/approval/1/meeting/10/topic")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{invalid:json:}"))
//                .andExpect(status().isBadRequest());
//
//        verify(topicService, never()).save(anyInt(), anyInt(), any(NewTopicDto.class));
//    }
//
//    // --- update Tests ---
//
//    @Test
//    void update_ShouldUpdateTopic_WhenValidData() throws Exception {
//        TopicDto updateDto = TopicDto.builder()
//                .id(1)
//                .meetingId(1)
//                .title("Updated Title").build();
//        when(topicService.update(anyInt(), anyInt(), anyInt(), any(NewTopicDto.class))).thenReturn(updateDto);
//
//        mockMvc.perform(patch("/approval/1/meeting/10/topic/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Updated Title"));
//
//        verify(topicService, times(1)).update(eq(1), eq(10), eq(1), any(NewTopicDto.class));
//    }
//
//    // --- delete Tests ---
//
//    @Test
//    void delete_ShouldRemoveTopic_WhenExists() throws Exception {
//        doNothing().when(topicService).delete(anyInt(), anyInt(), anyInt());
//
//        mockMvc.perform(delete("/approval/1/meeting/10/topic/1"))
//                .andExpect(status().isNoContent());
//
//        verify(topicService, times(1)).delete(1, 10, 1);
//    }
//
//    // --- Controller Layer Security & Logging (Indirect Tests) ---
//
//    @Test
//    void controllerMethods_ShouldDelegateToService() throws Exception {
//        // Just a sanity check that all methods call their respective service methods
//
//        // findAll
//        when(topicService.findAllByMeetingId(anyInt(), anyInt())).thenReturn(List.of(topicDto));
//        mockMvc.perform(get("/approval/1/meeting/10/topic")).andExpect(status().isOk());
//
//        // findById
//        when(topicService.findById(anyInt(), anyInt(), anyInt())).thenReturn(topicDto);
//        mockMvc.perform(get("/approval/1/meeting/10/topic/1")).andExpect(status().isOk());
//
//    // save
//        when(topicService.save(anyInt(), anyInt(), any(NewTopicDto.class))).thenReturn(topicDto);
//        mockMvc.perform(post("/approval/1/meeting/10/topic")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(topicDto)))
//                .andExpect(status().isCreated());
//
//        // update
//        when(topicService.update(anyInt(), anyInt(), anyInt(), any(NewTopicDto.class))).thenReturn(topicDto);
//        mockMvc.perform(patch("/approval/1/meeting/10/topic/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(topicDto)))
//                .andExpect(status().isOk());
//
//        // delete
//        doNothing().when(topicService).delete(anyInt(), anyInt(), anyInt());
//        mockMvc.perform(delete("/approval/1/meeting/10/topic/1")).andExpect(status().isNoContent());
//
//        // Verify all were called exactly once
//        verify(topicService, times(1)).findAllByMeetingId(1, 10);
//        verify(topicService, times(1)).findById(1, 10, 1);
//        verify(topicService, times(1)).save(eq(1), eq(10), any(NewTopicDto.class));
//        verify(topicService, times(1)).update(eq(1), eq(10), eq(1), any(NewTopicDto.class));
//        verify(topicService, times(1)).delete(1, 10, 1);
//    }
//}