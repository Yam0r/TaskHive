package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import my.app.files.model.Task;
import my.app.files.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Sql(scripts = {"classpath:database/task/add-test-task.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/task/clear-db-for-task.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testFindByAssigneeId_ShouldReturnTasks() {
        Page<Task> tasks = taskRepository.findByAssigneeId(1L, PageRequest.of(0, 10));
        assertThat(tasks.getContent()).hasSize(1);
    }
}
