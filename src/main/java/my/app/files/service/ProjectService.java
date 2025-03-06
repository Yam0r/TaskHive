package my.app.files.service;

import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    ProjectDto createANewProject(CreateProjectRequestDto createProjectRequestDto);

    List<ProjectDto> retrieveUsersProjects(Pageable pageable);

    ProjectDto retrieveProjectDetails(Long id);

    void updateProject(Long id, UpdateProjectRequestDto updateProjectRequestDto);

    void deleteProject(Long id);
}

