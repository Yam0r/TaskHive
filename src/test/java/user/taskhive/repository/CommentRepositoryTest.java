package user.taskhive.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import my.app.files.model.Comment;
import my.app.files.repository.CommentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Sql(scripts = "classpath:database/attachment/add-test-att.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/comment/clear-db-comment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldFindCommentsByTaskId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = commentRepository.findByTaskId(1L, pageable);
        List<Comment> comments = commentPage.getContent();

        assertThat(comments).isNotEmpty();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getContent()).isEqualTo("This is the first comment.");
    }

    @Test
    void shouldReturnEmptyListIfNoCommentsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = commentRepository.findByTaskId(999L, pageable);
        assertThat(commentPage.getContent()).isEmpty();
    }
}
