package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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
import my.app.files.service.impl.TasksServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    private static final String OWNER_EMAIL = "owner@example.com";

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private TasksServiceImpl tasksService;

    @Test
    void createANewTask_ValidRequest_SetsDefaultStatusProjectAndAssignee() {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setProjectId(1L);
        requestDto.setAssigneeId(2L);
        when(taskMapper.toEntity(requestDto)).thenReturn(new Task());
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(project));
        User assignee = TestDataUtil.createTestUser(2L, "assignee@example.com", "John", "Doe");
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        TaskDto taskDto = new TaskDto();
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = tasksService.createANewTask(requestDto, OWNER_EMAIL);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task savedTask = captor.getValue();
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.NOT_STARTED);
        assertThat(savedTask.getProject()).isSameAs(project);
        assertThat(savedTask.getAssignee()).isSameAs(assignee);
        assertThat(result).isSameAs(taskDto);
        verifyNoInteractions(labelRepository);
    }

    @Test
    void createANewTask_ProjectNotFoundOrNotOwned_ThrowsAndDoesNotSave() {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setProjectId(99L);
        when(taskMapper.toEntity(requestDto)).thenReturn(new Task());
        when(projectRepository.findByIdAndUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tasksService.createANewTask(requestDto, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Project not found");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void retrieveTaskDetails_ExistingId_ReturnsMappedTask() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(task));
        TaskDto taskDto = new TaskDto();
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        assertThat(tasksService.retrieveTaskDetails(1L, OWNER_EMAIL)).isSameAs(taskDto);
    }

    @Test
    void retrieveTaskDetails_NotFoundOrNotOwned_Throws() {
        when(taskRepository.findByIdAndProjectUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tasksService.retrieveTaskDetails(99L, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Task not found");
    }

    @Test
    void retrieveTasksForAProject_OwnProject_ReturnsMappedTasks() {
        Project project = TestDataUtil.createTestProject(1L);
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(project));
        Task task = TestDataUtil.createTestTask(project, "Task");
        PageRequest pageable = PageRequest.of(0, 10);
        when(taskRepository.findAllByProjectId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(task)));
        TaskDto taskDto = new TaskDto();
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        List<TaskDto> result = tasksService.retrieveTasksForAProject(pageable, 1L,
                OWNER_EMAIL);

        assertThat(result).containsExactly(taskDto);
    }

    @Test
    void retrieveTasksForAProject_NotOwnedProject_ThrowsAndDoesNotLoadTasks() {
        when(projectRepository.findByIdAndUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.empty());
        PageRequest pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> tasksService.retrieveTasksForAProject(pageable, 1L,
                OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
        verify(taskRepository, never()).findAllByProjectId(any(), any());
    }

    @Test
    void getMyTasks_ExistingUser_ReturnsTasksAssignedToUser() {
        User user = TestDataUtil.createTestUser(1L, "user@example.com", "John", "Doe");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        PageRequest pageable = PageRequest.of(0, 10);
        when(taskRepository.findByAssigneeId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(task)));
        TaskDto taskDto = new TaskDto();
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        List<TaskDto> result = tasksService.getMyTasks("user@example.com", pageable);

        assertThat(result).containsExactly(taskDto);
    }

    @Test
    void getMyTasks_UnknownUser_Throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tasksService.getMyTasks("missing@example.com",
                PageRequest.of(0, 10)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMyStatistics_CountsTasksByStatus() {
        User user = TestDataUtil.createTestUser(1L, "user@example.com", "John", "Doe");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findAllTasksByAssigneeId(1L)).thenReturn(List.of(
                taskWithStatus(TaskStatus.NOT_STARTED),
                taskWithStatus(TaskStatus.IN_PROGRESS),
                taskWithStatus(TaskStatus.COMPLETED),
                taskWithStatus(TaskStatus.COMPLETED)));

        TaskStatisticsDto stats = tasksService.getMyStatistics("user@example.com");

        assertThat(stats.getTotalTasks()).isEqualTo(4L);
        assertThat(stats.getNotStartedTasks()).isEqualTo(1L);
        assertThat(stats.getInProgressTasks()).isEqualTo(1L);
        assertThat(stats.getCompletedTasks()).isEqualTo(2L);
    }

    @Test
    void updateTask_ExistingId_AppliesChangesAndSaves() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(task));
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto();
        requestDto.setName("Updated Task");

        tasksService.updateTask(1L, requestDto, OWNER_EMAIL);

        verify(taskMapper).updateTaskFromDto(requestDto, task);
        verify(taskRepository).save(task);
    }

    @Test
    void assignLabelToTask_AddsLabelAndSaves() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(task));
        Label label = TestDataUtil.createTestLabel("Important", "red");
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));

        tasksService.assignLabelToTask(1L, 1L, OWNER_EMAIL);

        assertThat(task.getLabels()).containsExactly(label);
        verify(taskRepository).save(task);
    }

    @Test
    void removeLabelFromTask_RemovesLabelAndSaves() {
        Label label = TestDataUtil.createTestLabel("Important", "red");
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        task.getLabels().add(label);
        when(taskRepository.findByIdAndProjectUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(task));
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));

        tasksService.removeLabelFromTask(1L, 1L, OWNER_EMAIL);

        assertThat(task.getLabels()).isEmpty();
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_OwnTask_DeletesIt() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, OWNER_EMAIL))
                .thenReturn(Optional.of(task));

        tasksService.deleteTask(1L, OWNER_EMAIL);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_NotFoundOrNotOwned_ThrowsAndDoesNotDelete() {
        when(taskRepository.findByIdAndProjectUserEmail(99L, OWNER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tasksService.deleteTask(99L, OWNER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
        verify(taskRepository, never()).delete(any());
    }

    private Task taskWithStatus(TaskStatus status) {
        Task task = new Task();
        task.setStatus(status);
        return task;
    }
}
