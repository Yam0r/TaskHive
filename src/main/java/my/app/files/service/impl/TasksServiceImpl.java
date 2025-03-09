package my.app.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.mapper.TaskMapper;
import my.app.files.model.Project;
import my.app.files.model.Task;
import my.app.files.model.User;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.TasksService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TasksServiceImpl implements TasksService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TaskDto createANewTask(CreateTaskRequestDto createTaskRequestDto) {
        Task task = taskMapper.toEntity(createTaskRequestDto);

        if (createTaskRequestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(createTaskRequestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignee(assignee);
        }

        if (createTaskRequestDto.getProjectId() != null) {
            Project project = projectRepository.findById(createTaskRequestDto.getProjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
            task.setProject(project);
        } else {
            throw new IllegalArgumentException("Project ID is required");
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }


    @Override
    public List<TaskDto> retrieveTasksForAProject(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toDto)
                .getContent();
    }

    @Override
    public TaskDto retrieveTaskDetails(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return taskMapper.toDto(task);
    }

    @Override
    public void updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskMapper.updateTaskFromDto(updateTaskRequestDto, task);

        if (updateTaskRequestDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(updateTaskRequestDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignee(assignee);
        }

        taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
