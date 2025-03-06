package my.app.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.mapper.ProjectMapper;
import my.app.files.model.Project;
import my.app.files.model.User;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new IllegalStateException("User not authenticated");
        }
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public ProjectDto createANewProject(CreateProjectRequestDto createProjectRequestDto) {
        User user = getCurrentUser();
        Project project = projectMapper.toProject(createProjectRequestDto);
        project.setUser(user);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(savedProject);
    }

    @Override
    public List<ProjectDto> retrieveUsersProjects(Pageable pageable) {
        User user = getCurrentUser();
        return projectRepository.findByUserId(user.getId(), pageable).stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    public ProjectDto retrieveProjectDetails(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return projectMapper.toProjectDto(project);
    }

    @Override
    @Transactional
    public void updateProject(Long id, UpdateProjectRequestDto requestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (requestDto.getName() != null) {
            project.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            project.setDescription(requestDto.getDescription());
        }
        if (requestDto.getStartDate() != null) {
            project.setStartDate(requestDto.getStartDate());
        }
        if (requestDto.getEndDate() != null) {
            project.setEndDate(requestDto.getEndDate());
        }
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}

