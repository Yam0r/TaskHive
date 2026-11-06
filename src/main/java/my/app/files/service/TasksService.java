package my.app.files.service;

import java.util.List;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.TaskStatisticsDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import org.springframework.data.domain.Pageable;

public interface TasksService {
    TaskDto createANewTask(CreateTaskRequestDto createTaskRequestDto, String userEmail);

    List<TaskDto> retrieveTasksForAProject(Pageable pageable, Long projectId, String userEmail);

    TaskDto retrieveTaskDetails(Long id, String userEmail);

    void updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto, String userEmail);

    void deleteTask(Long id, String userEmail);

    TaskDto assignLabelToTask(Long taskId, Long labelId, String userEmail);

    TaskDto removeLabelFromTask(Long taskId, Long labelId, String userEmail);

    List<TaskDto> getMyTasks(String userEmail, Pageable pageable);

    TaskStatisticsDto getMyStatistics(String userEmail);
}
