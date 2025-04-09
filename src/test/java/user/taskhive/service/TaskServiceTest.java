package user.taskhive.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.exception.TaskNotFoundException;
import my.app.files.mapper.TaskMapper;
import my.app.files.model.Label;
import my.app.files.model.Project;
import my.app.files.model.Task;
import my.app.files.repository.LabelRepository;
import my.app.files.repository.ProjectRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.impl.TasksServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

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

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setName("Task for Testing");

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Task for Testing");
    }

    @Test
    void testRetrieveTaskDetails_ShouldReturnTaskDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = tasksService.retrieveTaskDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Task for Testing", result.getName());
    }

    @Test
    void testRetrieveTaskDetails_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> tasksService.retrieveTaskDetails(99L));
    }

    @Test
    void testCreateANewTask_ShouldReturnTaskDto() {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setName("New Task");
        requestDto.setProjectId(1L);

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskMapper.toEntity(requestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = tasksService.createANewTask(requestDto);

        assertNotNull(result);
        assertEquals("Task for Testing", result.getName());
    }

    @Test
    void testRetrieveTasksForAProject_ShouldReturnTaskList() {
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findAll(PageRequest.of(0, 10))).thenReturn(taskPage);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        List<TaskDto> result = tasksService.retrieveTasksForAProject(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals("Task for Testing", result.get(0).getName());
    }

    @Test
    void testAssignLabelToTask_ShouldAddLabel() {
        Label label = new Label();
        label.setId(1L);
        label.setName("Important");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = tasksService.assignLabelToTask(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void testRemoveLabelFromTask_ShouldRemoveLabel() {
        Label label = new Label();
        label.setId(1L);
        label.setName("Important");
        task.setLabels(new HashSet<>(Set.of(label)));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto result = tasksService.removeLabelFromTask(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void testDeleteTask_ShouldDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> tasksService.deleteTask(1L));
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTask_ShouldThrowException() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> tasksService.deleteTask(99L));
    }
}
