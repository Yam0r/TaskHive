package my.app.files.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createANewProject(@RequestBody @Valid CreateProjectRequestDto requestDto) {
        return ResponseEntity.ok(projectService.createANewProject(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> retrieveUsersProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.retrieveUsersProjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> retrieveProjectDetails(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.retrieveProjectDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable Long id, @RequestBody @Valid UpdateProjectRequestDto requestDto) {
        projectService.updateProject(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
