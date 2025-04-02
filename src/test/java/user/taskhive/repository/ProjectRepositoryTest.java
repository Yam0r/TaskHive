package user.taskhive.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import my.app.files.model.Project;
import my.app.files.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Sql(scripts = "classpath:database/project/add-test-project.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/project/clear-db-for-project.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testFindByUserId() {
        List<Project> projects = projectRepository.findByUserId(1L, PageRequest.of(0, 10));
        assertEquals(1, projects.size());
        assertEquals("Test Project", projects.get(0).getName());
    }
}
