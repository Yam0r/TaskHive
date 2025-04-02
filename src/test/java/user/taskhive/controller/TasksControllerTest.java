package user.taskhive.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.service.TasksService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TasksService tasksService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "testUser", roles = {"USER"})
    @Test
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/add-test-task.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateTask_ShouldReturnCreated() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setName("New Task");
        requestDto.setProjectId(2L);

        TaskDto responseDto = new TaskDto();
        responseDto.setId(2L);
        responseDto.setName("New Task");

        when(tasksService.createANewTask(any(CreateTaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Task"));
    }

    @WithMockUser(username = "testUser", roles = {"USER"})
    @Test
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/add-test-task.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testRetrieveTaskDetails() throws Exception {
        TaskDto task = new TaskDto();
        task.setId(1L);
        task.setName("Task for Testing");

        when(tasksService.retrieveTaskDetails(1L)).thenReturn(task);

        mockMvc.perform(get("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Task for Testing"));
    }

    @WithMockUser(username = "testUser", roles = {"USER"})
    @Test
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/add-test-task.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateTask() throws Exception {
        UpdateTaskRequestDto updateDto = new UpdateTaskRequestDto();
        updateDto.setName("Updated Task");

        doNothing().when(tasksService).updateTask(1L, updateDto);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @WithMockUser(username = "testUser", roles = {"USER"})
    @Test
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/task/add-test-task.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/task/clear-db-for-task.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteTask() throws Exception {
        doNothing().when(tasksService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
