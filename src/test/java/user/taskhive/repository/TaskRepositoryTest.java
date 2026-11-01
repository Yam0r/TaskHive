package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import my.app.files.model.Task;
import my.app.files.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByAssigneeId_AssigneeWithTask_ReturnsIt() {
        assertThat(taskRepository.findByAssigneeId(1L, PageRequest.of(0, 10)).getContent())
                .extracting(Task::getName)
                .containsExactly("Task for Testing");
    }

    @Test
    void findByAssigneeId_UserWithoutTasks_ReturnsEmptyPage() {
        assertThat(taskRepository.findByAssigneeId(2L, PageRequest.of(0, 10))).isEmpty();
    }

    @Test
    void findAllTasksByAssigneeId_ReturnsAllTasksWithoutPaging() {
        assertThat(taskRepository.findAllTasksByAssigneeId(1L)).hasSize(1);
    }

    @Test
    void findAllByProjectId_ReturnsTasksOfProject() {
        assertThat(taskRepository.findAllByProjectId(1L, PageRequest.of(0, 10))).hasSize(1);
    }
}
