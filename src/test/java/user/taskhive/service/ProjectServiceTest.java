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
    void retrieveProjectDetails_ExistingId_ReturnsMappedProject() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        assertThat(projectService.retrieveProjectDetails(1L)).isSameAs(projectDto);
    }

    @Test
    void retrieveProjectDetails_NonExistingId_Throws() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.retrieveProjectDetails(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Project not found with id: 99");
    }

    @Test
    void updateProject_ExistingId_AppliesChangesAndSaves() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        UpdateProjectRequestDto requestDto = new UpdateProjectRequestDto();
        requestDto.setName("Updated Name");

        projectService.updateProject(1L, requestDto);

        verify(projectMapper).updateProjectFromDto(requestDto, project);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_NonExistingId_ThrowsAndDoesNotSave() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(99L, new UpdateProjectRequestDto()))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_ExistingId_DeletesIt() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        projectService.deleteProject(1L);

        verify(projectRepository).deleteById(1L);
    }

    @Test
    void deleteProject_NonExistingId_ThrowsAndDoesNotDelete() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProject(99L))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).deleteById(any());
    }
}
