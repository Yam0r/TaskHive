package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.TaskStatisticsDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.service.TasksService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tasks", description = "Manages tasks within projects, "
        + "including creation, retrieval, updating, and deletion.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TasksController {
    private final TasksService tasksService;

    @Operation(summary = "Create a new task",
            description = "Creates a new task within a project.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<TaskDto> createANewTask(
            @RequestBody @Valid CreateTaskRequestDto createTaskRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tasksService.createANewTask(createTaskRequestDto,
                        userDetails.getUsername()));
    }

    @Operation(summary = "Get tasks for a project",
            description = "Retrieves a paginated list of tasks for a specific project.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<TaskDto>> retrieveTasksForAProject(
            Pageable pageable, Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tasksService.retrieveTasksForAProject(pageable, projectId,
                userDetails.getUsername()));
    }

    @Operation(summary = "Get task details",
            description = "Retrieves detailed information about a specific task.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> retrieveTaskDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tasksService.retrieveTaskDetails(id,
                userDetails.getUsername()));
    }

    @Operation(summary = "Get my tasks",
            description = "Retrieves tasks assigned to the current user.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getMyTasks(
            @AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(tasksService.getMyTasks(userDetails.getUsername(), pageable));
    }

    @Operation(summary = "Get my task statistics",
            description = "Returns task counts by status for the current user.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/my/statistics")
    public ResponseEntity<TaskStatisticsDto> getMyStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tasksService.getMyStatistics(userDetails.getUsername()));
    }

    @Operation(summary = "Update a task", description = "Updates an existing task by its ID.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTaskRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        tasksService.updateTask(id, requestDto, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tasksService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign a label to a task",
            description = "Associates a specific label with a task.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskDto> assignLabelToTask(
            @PathVariable Long taskId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tasksService.assignLabelToTask(taskId, labelId,
                userDetails.getUsername()));
    }

    @Operation(summary = "Remove a label from a task",
            description = "Removes the association between a label and a task.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskDto> removeLabelFromTask(
            @PathVariable Long taskId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tasksService.removeLabelFromTask(taskId, labelId,
                userDetails.getUsername()));
    }
}
