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
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.service.ProjectService;
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
class ProjectControllerTest {
    private static final String USER_EMAIL = "testuser@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void createProject_ValidRequest_ReturnsCreatedProject() throws Exception {
        when(projectService.createANewProject(any(CreateProjectRequestDto.class), eq(USER_EMAIL)))
                .thenReturn(createProjectDto());
        CreateProjectRequestDto requestDto = createValidRequest();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Project"));

        verify(projectService).createANewProject(
                argThat(dto -> "New Project".equals(dto.getName())), eq(USER_EMAIL));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProject_StartDateInPast_ReturnsBadRequest() throws Exception {
        CreateProjectRequestDto requestDto = createValidRequest();
        requestDto.setStartDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getProjects_ReturnsProjectsOfCurrentUser() throws Exception {
        when(projectService.retrieveUsersProjects(any(Pageable.class), eq(USER_EMAIL)))
                .thenReturn(List.of(createProjectDto()));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("New Project"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProject_ExistingId_ReturnsProject() throws Exception {
        when(projectService.retrieveProjectDetails(1L)).thenReturn(createProjectDto());

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProject_NonExistingId_ReturnsNotFound() throws Exception {
        when(projectService.retrieveProjectDetails(99L))
                .thenThrow(new EntityNotFoundException("Project not found with id: 99"));

        mockMvc.perform(get("/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateProject_ValidRequest_ReturnsNoContent() throws Exception {
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        requestDto.setName("Updated Name");

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());

        verify(projectService).updateProject(eq(1L),
                argThat(dto -> "Updated Name".equals(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateProject_TooShortName_ReturnsBadRequest() throws Exception {
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        requestDto.setName("ab");

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteProject_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(1L);
    }

    private CreateProjectRequestDto createValidRequest() {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        requestDto.setName("New Project");
        requestDto.setDescription("A description");
        requestDto.setStartDate(LocalDate.now());
        requestDto.setEndDate(LocalDate.now().plusMonths(6));
        return requestDto;
    }

    private ProjectDto createProjectDto() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("New Project");
        projectDto.setDescription("A description");
        return projectDto;
    }
}
