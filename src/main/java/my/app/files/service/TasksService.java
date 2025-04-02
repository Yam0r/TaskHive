package my.app.files.service;

import java.util.List;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import org.springframework.data.domain.Pageable;

public interface TasksService {
    TaskDto createANewTask(CreateTaskRequestDto createTaskRequestDto);

    List<TaskDto> retrieveTasksForAProject(Pageable pageable);

    TaskDto retrieveTaskDetails(Long id);

    void updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto);

    void deleteTask(Long id);

    TaskDto assignLabelToTask(Long taskId, Long labelId);

    TaskDto removeLabelFromTask(Long taskId, Long labelId);
}
