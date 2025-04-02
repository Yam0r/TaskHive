package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Project",
        description = "Manages user projects, including creation, "
                + "retrieval, updating, and deletion.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Create a new project",
            description = "Creates a new project for the user.")
    @PostMapping
    public ResponseEntity<ProjectDto> createANewProject(@RequestBody @Valid
                                                            CreateProjectRequestDto requestDto) {
        return ResponseEntity.ok(projectService.createANewProject(requestDto));
    }

    @Operation(summary = "Get user projects",
            description = "Retrieves a paginated list "
                    + "of projects belonging to the authenticated user.")
    @GetMapping
    public ResponseEntity<List<ProjectDto>> retrieveUsersProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.retrieveUsersProjects(pageable));
    }

    @Operation(summary = "Get project details",
            description = "Retrieves detailed information about a specific project.")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> retrieveProjectDetails(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.retrieveProjectDetails(id));
    }

    @Operation(summary = "Update a project",
            description = "Updates an existing project by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable Long id,
                                              @RequestBody
                                              @Valid UpdateProjectRequestDto requestDto) {
        projectService.updateProject(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a project", description = "Deletes a project by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
