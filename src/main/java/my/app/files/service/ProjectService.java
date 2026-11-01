package my.app.files.service;

import java.util.List;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto createANewProject(CreateProjectRequestDto createProjectRequestDto, String userEmail);

    List<ProjectDto> retrieveUsersProjects(Pageable pageable, String userEmail);

    ProjectDto retrieveProjectDetails(Long id);

    void updateProject(Long id, UpdateProjectRequestDto requestDto);

    void deleteProject(Long id);
}

