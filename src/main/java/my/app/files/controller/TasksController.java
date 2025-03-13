package my.app.files.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.service.TasksService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TasksController {
    private final TasksService tasksService;

    @PostMapping
    public ResponseEntity<TaskDto> createANewTask(@RequestBody @Valid CreateTaskRequestDto
                                                              createTaskRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tasksService.createANewTask(createTaskRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> retrieveTasksForAProject(Pageable pageable) {
        return ResponseEntity.ok(tasksService.retrieveTasksForAProject(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> retrieveTaskDetails(@PathVariable Long id) {
        return ResponseEntity.ok(tasksService.retrieveTaskDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id,
                                           @RequestBody @Valid UpdateTaskRequestDto requestDto) {
        tasksService.updateTask(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        tasksService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskDto> assignLabelToTask(@PathVariable Long taskId,
                                                     @PathVariable Long labelId) {
        return ResponseEntity.ok(tasksService.assignLabelToTask(taskId, labelId));
    }

    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskDto> removeLabelFromTask(@PathVariable Long taskId,
                                                       @PathVariable Long labelId) {
        return ResponseEntity.ok(tasksService.removeLabelFromTask(taskId, labelId));
    }
}
