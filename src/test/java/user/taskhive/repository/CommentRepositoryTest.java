package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import my.app.files.model.Comment;
import my.app.files.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByTaskId_TaskWithComments_ReturnsAllOfThem() {
        assertThat(commentRepository.findByTaskId(1L, PageRequest.of(0, 10)).getContent())
                .extracting(Comment::getContent)
                .containsExactlyInAnyOrder("This is the first comment.",
                        "This is the second comment.");
    }

    @Test
    void findByTaskId_UnknownTask_ReturnsEmptyPage() {
        assertThat(commentRepository.findByTaskId(999L, PageRequest.of(0, 10))).isEmpty();
    }
}
