package user.taskhive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import my.app.files.dto.project.CreateProjectRequestDto;
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
class ProjectApiIntegrationTest {
    private static final String PROJECT_BY_NAME_QUERY =
            "SELECT user_id, status FROM projects WHERE name = ?";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void createProject_SavesProjectOwnedByCurrentUser() throws Exception {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        requestDto.setName("My Project");
        requestDto.setStartDate(LocalDate.now());
        requestDto.setEndDate(LocalDate.now().plusMonths(1));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("My Project"));

        Map<String, Object> project = jdbcTemplate.queryForMap(PROJECT_BY_NAME_QUERY, "My Project");
        assertThat(project).containsEntry("user_id", 1L)
                .containsEntry("status", "INITIATED");
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getProjects_ReturnsOnlyProjectsOfCurrentUser() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getProjects_UserWithoutProjects_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
