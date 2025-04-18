package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.model.User;
import my.app.files.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Project", description = "Manages user projects, including creation, "
        + "retrieval, updating, and deletion.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Create a new project",
            description = "Creates a new project for the user.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectDto> createANewProject(
            @RequestBody @Valid CreateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        ProjectDto createdProject = projectService.createANewProject(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @Operation(summary = "Get user projects",
            description = "Retrieves a paginated list of projects belonging "
                    + "to the authenticated user.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProjectDto>> retrieveUsersProjects(
            Pageable pageable,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.retrieveUsersProjects(pageable, user));
    }

    @Operation(summary = "Get project details",
            description = "Retrieves detailed information about a specific project.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> retrieveProjectDetails(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.retrieveProjectDetails(id));
    }

    @Operation(summary = "Update a project",
            description = "Updates an existing project by its ID.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProjectRequestDto requestDto) {
        projectService.updateProject(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a project", description = "Deletes a project by its ID.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
