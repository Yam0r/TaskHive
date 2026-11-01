package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import my.app.files.model.User;
import my.app.files.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> user = userRepository.findByEmail("admin@example.com");

        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("TestAdmin");
    }

    @Test
    void findByEmail_UnknownEmail_ReturnsEmpty() {
        assertThat(userRepository.findByEmail("missing@example.com")).isEmpty();
    }

    @Test
    void existsByEmail_ReturnsTrueOnlyForExistingEmail() {
        assertThat(userRepository.existsByEmail("testuser@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }
}
