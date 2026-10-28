package my.app.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.TaskStatisticsDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.mapper.TaskMapper;
import my.app.files.model.Label;
import my.app.files.model.Project;
import my.app.files.model.Task;
import my.app.files.model.TaskStatus;
import my.app.files.model.User;
import my.app.files.repository.LabelRepository;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.TasksService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class TasksServiceImpl implements TasksService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;

    @Override
    @Transactional
    public TaskDto createANewTask(CreateTaskRequestDto createTaskRequestDto) {
        Task task = taskMapper.toEntity(createTaskRequestDto);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NOT_STARTED);
        }

        Optional.ofNullable(createTaskRequestDto.getAssigneeId())
                .map(this::getUserById)
                .ifPresent(task::setAssignee);

        task.setProject(getProjectById(createTaskRequestDto.getProjectId()));

        Optional.ofNullable(createTaskRequestDto.getLabelIds())
                .map(labelRepository::findAllById)
                .ifPresent(labels -> task.setLabels(new HashSet<>(labels)));

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskDto> retrieveTasksForAProject(Pageable pageable, Long projectId) {
        return taskRepository.findAllByProjectId(projectId, pageable)
                .map(taskMapper::toDto)
                .getContent();
    }

    @Override
    public List<TaskDto> getMyTasks(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return taskRepository.findByAssigneeId(user.getId(), pageable)
                .map(taskMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public TaskDto retrieveTaskDetails(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskMapper.updateTaskFromDto(updateTaskRequestDto, task);

        Optional.ofNullable(updateTaskRequestDto.getAssigneeId())
                .map(this::getUserById)
                .ifPresent(task::setAssignee);

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskStatisticsDto getMyStatistics(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Task> userTasks = taskRepository.findAllTasksByAssigneeId(user.getId());
        TaskStatisticsDto stats = new TaskStatisticsDto();

        stats.setTotalTasks((long) userTasks.size());

        stats.setNotStartedTasks(countByStatus(userTasks, TaskStatus.NOT_STARTED));
        stats.setInProgressTasks(countByStatus(userTasks, TaskStatus.IN_PROGRESS));
        stats.setCompletedTasks(countByStatus(userTasks, TaskStatus.COMPLETED));
        return stats;
    }

    @Override
    @Transactional
    public TaskDto assignLabelToTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Label label = getLabelById(labelId);

        task.getLabels().add(label);
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDto removeLabelFromTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Label label = getLabelById(labelId);

        task.getLabels().remove(label);
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private Label getLabelById(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
    }

    private long countByStatus(List<Task> tasks, TaskStatus status) {
        return tasks.stream().filter(t -> t.getStatus() == status).count();
    }
}
