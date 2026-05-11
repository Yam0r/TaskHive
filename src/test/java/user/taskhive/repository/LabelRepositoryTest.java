package user.taskhive.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import user.taskhive.config.TestDataUtil;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class LabelRepositoryTest {

    @Autowired
    private LabelRepository labelRepository;

    @Sql(scripts = "classpath:database/label/add-test-label.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/label/clear-db-for-labels.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void shouldFindLabelByName() {
        Optional<Label> existingLabel = labelRepository.findByName("Important");

        Label label;
        if (existingLabel.isPresent()) {
            label = existingLabel.get();
        } else {
            label = TestDataUtil.createTestLabel("Important", "white");
            labelRepository.save(label);
        }

        Optional<Label> foundLabel = labelRepository.findByName("Important");

        Assertions.assertTrue(foundLabel.isPresent());
        Assertions.assertEquals("Important", foundLabel.get().getName());
        Assertions.assertEquals("white", foundLabel.get().getColor());
    }

    @Test
    void shouldNotFindLabelByName() {
        Label foundLabel = labelRepository.findByName("NonExisting").orElse(null);
        assertThat(foundLabel).isNull();
    }
}
