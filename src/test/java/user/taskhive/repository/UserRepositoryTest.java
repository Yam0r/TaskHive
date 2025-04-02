package user.taskhive.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import my.app.files.model.User;
import my.app.files.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Sql(scripts = {"classpath:database/user/add-test-user.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/user/clear-db-for-user.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testFindByEmail_ShouldReturnUser() {
        Optional<User> user = userRepository.findByEmail("admin@example.com");
        assertTrue(user.isPresent());
        assertEquals("TestAdmin", user.get().getUsername());
    }

    @Sql(scripts = {"classpath:database/user/clear-db-for-user.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/user/add-test-user.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/user/clear-db-for-user.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testExistsByEmail_ShouldReturnTrue() {
        assertTrue(userRepository.existsByEmail("testuser@example.com"));
    }
}
