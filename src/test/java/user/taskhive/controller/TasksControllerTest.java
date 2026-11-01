package user.taskhive.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.TaskStatisticsDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.service.TasksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TasksControllerTest {
    private static final String USER_EMAIL = "user@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TasksService tasksService;

    @Test
    @WithMockUser(roles = "USER")
    void createTask_ValidRequest_ReturnsCreatedTask() throws Exception {
        when(tasksService.createANewTask(any(CreateTaskRequestDto.class)))
                .thenReturn(createTaskDto(10L, "New Task"));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Task"));

        verify(tasksService).createANewTask(argThat(dto -> "New Task".equals(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTask_BlankName_ReturnsBadRequest() throws Exception {
        CreateTaskRequestDto requestDto = createValidRequest();
        requestDto.setName("");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tasksService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTasksForProject_PassesProjectIdAndPaging() throws Exception {
        when(tasksService.retrieveTasksForAProject(any(Pageable.class), eq(1L)))
                .thenReturn(List.of(createTaskDto(1L, "Task for Testing")));

        mockMvc.perform(get("/tasks")
                        .param("projectId", "1")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Task for Testing"));

        verify(tasksService).retrieveTasksForAProject(
                argThat(pageable -> pageable.getPageNumber() == 2 && pageable.getPageSize() == 5),
                eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTask_ExistingId_ReturnsTask() throws Exception {
        when(tasksService.retrieveTaskDetails(1L)).thenReturn(createTaskDto(1L, "Task"));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Task"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTask_NonExistingId_ReturnsNotFound() throws Exception {
        when(tasksService.retrieveTaskDetails(99L))
                .thenThrow(new EntityNotFoundException("Task not found"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getMyTasks_UsesEmailOfCurrentUser() throws Exception {
        when(tasksService.getMyTasks(eq(USER_EMAIL), any(Pageable.class)))
                .thenReturn(List.of(createTaskDto(1L, "My Task")));

        mockMvc.perform(get("/tasks/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Task"));

        verify(tasksService).getMyTasks(eq(USER_EMAIL), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getMyStatistics_ReturnsCountsOfCurrentUser() throws Exception {
        TaskStatisticsDto stats = new TaskStatisticsDto();
        stats.setTotalTasks(4L);
        stats.setNotStartedTasks(1L);
        stats.setInProgressTasks(1L);
        stats.setCompletedTasks(2L);
        when(tasksService.getMyStatistics(USER_EMAIL)).thenReturn(stats);

        mockMvc.perform(get("/tasks/my/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(4))
                .andExpect(jsonPath("$.notStartedTasks").value(1))
                .andExpect(jsonPath("$.inProgressTasks").value(1))
                .andExpect(jsonPath("$.completedTasks").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateTask_ValidRequest_ReturnsNoContent() throws Exception {
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto();
        requestDto.setName("Updated Task");

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());

        verify(tasksService).updateTask(eq(1L),
                argThat(dto -> "Updated Task".equals(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteTask_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(tasksService).deleteTask(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignLabel_ReturnsUpdatedTask() throws Exception {
        when(tasksService.assignLabelToTask(1L, 3L)).thenReturn(createTaskDto(1L, "Task"));

        mockMvc.perform(post("/tasks/1/labels/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(tasksService).assignLabelToTask(1L, 3L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void removeLabel_ReturnsUpdatedTask() throws Exception {
        when(tasksService.removeLabelFromTask(1L, 3L)).thenReturn(createTaskDto(1L, "Task"));

        mockMvc.perform(delete("/tasks/1/labels/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(tasksService).removeLabelFromTask(1L, 3L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignLabel_UnknownLabel_ReturnsNotFound() throws Exception {
        when(tasksService.assignLabelToTask(1L, 99L))
                .thenThrow(new EntityNotFoundException("Label not found"));

        mockMvc.perform(post("/tasks/1/labels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTask_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(tasksService);
    }

    private CreateTaskRequestDto createValidRequest() {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setName("New Task");
        requestDto.setProjectId(1L);
        requestDto.setAssigneeId(1L);
        requestDto.setDueDate(LocalDate.now().plusDays(5));
        return requestDto;
    }

    private TaskDto createTaskDto(Long id, String name) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setName(name);
        return taskDto;
    }
}
