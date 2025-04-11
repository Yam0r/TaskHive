package my.app.files.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.exception.ProjectNotFoundException;
import my.app.files.mapper.ProjectMapper;
import my.app.files.model.Project;
import my.app.files.model.User;
import my.app.files.repository.ProjectRepository;
import my.app.files.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto createANewProject(CreateProjectRequestDto createProjectRequestDto,
                                        User user) {
        Project project = projectMapper.toProject(createProjectRequestDto);
        project.setUser(user);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> retrieveUsersProjects(Pageable pageable, User user) {
        return projectRepository.findByUserId(user.getId(), pageable).stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto retrieveProjectDetails(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "
                        + id));
        return projectMapper.toProjectDto(project);
    }

    @Override
    public void updateProject(Long id, UpdateProjectRequestDto requestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "
                        + id));
        projectMapper.updateProjectFromDto(requestDto, project);
        projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
