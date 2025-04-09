package my.app.files.service.impl;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.exception.LabelNotFoundException;
import my.app.files.exception.ProjectNotFoundException;
import my.app.files.exception.TaskNotFoundException;
import my.app.files.exception.UserNotFoundException;
import my.app.files.mapper.TaskMapper;
import my.app.files.model.Label;
import my.app.files.model.Project;
import my.app.files.model.Task;
import my.app.files.model.User;
import my.app.files.repository.LabelRepository;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.TasksService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TasksServiceImpl implements TasksService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    private Label getLabelById(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
    }

    @Override
    public TaskDto createANewTask(CreateTaskRequestDto createTaskRequestDto) {
        Task task = taskMapper.toEntity(createTaskRequestDto);

        if (createTaskRequestDto.getAssigneeId() != null) {
            task.setAssignee(getUserById(createTaskRequestDto.getAssigneeId()));
        }

        if (createTaskRequestDto.getProjectId() != null) {
            task.setProject(getProjectById(createTaskRequestDto.getProjectId()));
        } else {
            throw new IllegalArgumentException("Project ID is required");
        }

        if (createTaskRequestDto.getLabelIds() != null) {
            List<Label> labels = labelRepository.findAllById(createTaskRequestDto.getLabelIds());
            task.setLabels(new HashSet<>(labels));
        }

        return taskMapper.toDto(taskRepository.save(task));
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
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return taskMapper.toDto(task);
    }

    @Override
    public void updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskMapper.updateTaskFromDto(updateTaskRequestDto, task);

        if (updateTaskRequestDto.getAssigneeId() != null) {
            task.setAssignee(getUserById(updateTaskRequestDto.getAssigneeId()));
        }

        taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto assignLabelToTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Label label = getLabelById(labelId);

        task.getLabels().add(label);
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto removeLabelFromTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Label label = getLabelById(labelId);

        task.getLabels().remove(label);
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }
}
