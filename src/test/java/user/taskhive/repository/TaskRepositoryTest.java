package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import my.app.files.model.Task;
import my.app.files.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
        List<Task> tasks = taskRepository.findByAssigneeId(1L, null);
        assertThat(tasks).hasSize(1);
    }
}
