package user.taskhive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.mapper.ProjectMapper;
import my.app.files.model.Project;
import my.app.files.model.User;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private CreateProjectRequestDto requestDto;
    private User testUser;
    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateProjectRequestDto();
        requestDto.setName("Test Project");
        requestDto.setDescription("Test description");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("testuser@example.com");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setUser(testUser);

        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project");
    }

    private void setUpAuthentication() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser@example.com")
                .password("password")
                .roles("USER")
                .build();

        Authentication authentication = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void retrieveProjectDetails_shouldReturnProjectDto_whenProjectExists() {
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.retrieveProjectDetails(projectId);

        assertNotNull(result);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getName(), result.getName());

        verify(projectRepository).findById(projectId);
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    void updateProject_shouldUpdateProject_whenProjectExists() {
        Long projectId = 1L;
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        requestDto.setName("Updated Name");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        projectService.updateProject(projectId, requestDto);

        assertEquals("Test Project", project.getName());
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(project);
    }

    @Test
    void deleteProject_shouldDeleteProject_whenProjectExists() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        projectService.deleteProject(projectId);

        verify(projectRepository).existsById(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Sql(scripts = "classpath:database/project/add-test-project.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/project/clear-db-for-project.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testCreateANewProject() {
        setUpAuthentication();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(projectMapper.toProject(requestDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toProjectDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createANewProject(requestDto);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Sql(scripts = "classpath:database/project/add-test-project.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/project/clear-db-for-project.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRetrieveUsersProjects() {
        setUpAuthentication();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserId(eq(testUser.getId()),
                eq(PageRequest.of(0, 10)))).thenReturn(List.of(new Project()));

        var result = projectService.retrieveUsersProjects(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findByUserId(eq(testUser.getId()),
                eq(PageRequest.of(0, 10)));
    }
}
