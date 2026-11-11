package user.taskhive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import my.app.files.dto.task.CreateTaskRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TaskApiIntegrationTest {
    private static final String OWNER = "testuser@example.com";
    private static final String STRANGER = "admin@example.com";
    private static final String COUNT_TASKS_QUERY = "SELECT COUNT(*) FROM tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = OWNER, roles = "USER")
    void getTask_Own_ReturnsOk() throws Exception {
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Task for Testing"));
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void getTask_OfAnotherUsersProject_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void getTasksForProject_OfAnotherUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks").param("projectId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void createTask_InAnotherUsersProject_ReturnsNotFoundAndDoesNotSave() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setName("Injected task");
        requestDto.setDueDate(LocalDate.now().plusDays(7));
        requestDto.setAssigneeId(2L);
        requestDto.setProjectId(1L);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        assertThat(jdbcTemplate.queryForObject(COUNT_TASKS_QUERY, Integer.class)).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void deleteTask_OfAnotherUsersProject_ReturnsNotFoundAndKeepsTask() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());

        assertThat(jdbcTemplate.queryForObject(COUNT_TASKS_QUERY, Integer.class)).isEqualTo(1);
    }
}
