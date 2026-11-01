package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import my.app.files.model.Project;
import my.app.files.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findByUserId_OwnerWithProject_ReturnsIt() {
        List<Project> projects = projectRepository.findByUserId(1L, PageRequest.of(0, 10));

        assertThat(projects).extracting(Project::getName).containsExactly("Test Project");
    }

    @Test
    void findByUserId_UserWithoutProjects_ReturnsEmptyList() {
        assertThat(projectRepository.findByUserId(2L, PageRequest.of(0, 10))).isEmpty();
    }
}
