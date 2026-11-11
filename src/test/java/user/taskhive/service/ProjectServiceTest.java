package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    private static final String OWNER_EMAIL = "owner@example.com";

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void createANewProject_SetsCurrentUserAsOwner() {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        when(projectMapper.toProject(requestDto)).thenReturn(new Project());
        User owner = TestDataUtil.createTestUser(1L, OWNER_EMAIL, "John", "Doe");
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toProjectDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createANewProject(requestDto, OWNER_EMAIL);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(owner);
        assertThat(result).isSameAs(projectDto);
    }

    @Test
    void createANewProject_UnknownUser_ThrowsAndDoesNotSave() {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto();
        when(projectMapper.toProject(requestDto)).thenReturn(new Project());
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.createANewProject(requestDto,
                "missing@example.com"))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void retrieveUsersProjects_ReturnsMappedProjectsOfUser() {
        User owner = TestDataUtil.createTestUser(1L, OWNER_EMAIL, "John", "Doe");
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner));
        Project project = TestDataUtil.createTestProject(1L);
        PageRequest pageable = PageRequest.of(0, 10);
        when(projectRepository.findByUserId(1L, pageable)).thenReturn(List.of(project));
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.retrieveUsersProjects(pageable, OWNER_EMAIL);

        assertThat(result).containsExactly(projectDto);
    }

    @Test
    void retrieveProjectDetails_OwnProject_ReturnsMappedProject() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(project));
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        assertThat(projectService.retrieveProjectDetails(1L, OWNER_EMAIL)).isSameAs(projectDto);
    }

    @Test
    void retrieveProjectDetails_NotFoundOrNotOwned_Throws() {
        when(projectRepository.findByIdAndUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.retrieveProjectDetails(99L, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Project not found with id: 99");
    }

    @Test
    void updateProject_OwnProject_AppliesChangesAndSaves() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(project));
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        requestDto.setName("Updated Name");

        projectService.updateProject(1L, requestDto, OWNER_EMAIL);

        verify(projectMapper).updateProjectFromDto(requestDto, project);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_NotFoundOrNotOwned_ThrowsAndDoesNotSave() {
        when(projectRepository.findByIdAndUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();

        assertThatThrownBy(() -> projectService.updateProject(99L, requestDto, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_OwnProject_DeletesIt() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(project));

        projectService.deleteProject(1L, OWNER_EMAIL);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_NotFoundOrNotOwned_ThrowsAndDoesNotDelete() {
        when(projectRepository.findByIdAndUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(99L, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).delete(any());
    }
}
