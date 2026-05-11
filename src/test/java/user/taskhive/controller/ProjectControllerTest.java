package user.taskhive.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import my.app.files.controller.ProjectController;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ProjectService projectService;

    @BeforeEach
    void setUp(@Autowired ProjectController projectController) {
        projectService = Mockito.mock(ProjectService.class);
        ReflectionTestUtils.setField(projectController, "projectService", projectService);
    }

    @Sql(scripts = "classpath:database/project/add-test-project.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/project/clear-db-for-project.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "TestUser", roles = "USER")
    @Test
    void testCreateANewProject() throws Exception {
        LocalDate today = LocalDate.now();

        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        requestDto.setName("New Project");
        requestDto.setDescription("A description");
        requestDto.setStartDate(today);
        requestDto.setEndDate(LocalDate.of(2027, 6, 21));

        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("New Project");
        projectDto.setDescription("A description");
        projectDto.setStartDate(today);
        projectDto.setEndDate(LocalDate.of(2027, 6, 21));

        when(projectService.createANewProject(any(CreateProjectRequestDto.class), any()))
                .thenReturn(projectDto);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Project\", "
                                + "\"description\": \"A description\", "
                                + "\"startDate\": \"" + today + "\", "
                                + "\"endDate\": \"2027-06-21\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Project"))
                .andExpect(jsonPath("$.description").value("A description"))
                .andExpect(jsonPath("$.startDate").value(today.toString()))
                .andExpect(jsonPath("$.endDate").value("2027-06-21"));

        verify(projectService, times(1))
                .createANewProject(any(CreateProjectRequestDto.class), any());
    }

    @WithMockUser(username = "TestUser", roles = "USER")
    @Test
    void testRetrieveUsersProjects() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "TestUser", roles = "USER")
    @Test
    void testRetrieveProjectDetails() throws Exception {
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "TestUser", roles = "USER")
    @Test
    void testUpdateProject() throws Exception {
        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Name\","
                                + " \"description\": \"Updated Description\","
                                + " \"startDate\": \"2025-03-21\","
                                + " \"endDate\": \"2025-06-21\"}"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "TestUser", roles = "USER")
    @Test
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());
    }
}
