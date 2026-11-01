package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class LabelRepositoryTest {

    @Autowired
    private LabelRepository labelRepository;

    @Test
    void findByName_ExistingLabel_ReturnsIt() {
        Optional<Label> label = labelRepository.findByName("Important");

        assertThat(label).isPresent();
        assertThat(label.get().getColor()).isEqualTo("white");
    }

    @Test
    void findByName_UnknownLabel_ReturnsEmpty() {
        assertThat(labelRepository.findByName("NonExisting")).isEmpty();
    }
}
