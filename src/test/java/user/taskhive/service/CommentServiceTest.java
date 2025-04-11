package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;
import my.app.files.mapper.CommentMapper;
import my.app.files.model.Comment;
import my.app.files.model.Task;
import my.app.files.model.User;
import my.app.files.repository.CommentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import user.taskhive.config.TestDataUtil;

@SpringBootTest
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void shouldAddComment() {
        CreateCommentRequestDto dto = new CreateCommentRequestDto();
        dto.setTaskId(1L);
        dto.setUserId(1L);
        dto.setContent("This is a new comment.");

        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L),
                "Test Task");
        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setContent("This is a new comment.");
        comment.setTask(task);
        comment.setAuthor(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a new comment.");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto addedComment = commentService.addComment(dto);

        assertThat(addedComment).isNotNull();
        assertThat(addedComment.getContent()).isEqualTo("This is a new comment.");

        verify(taskRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        CreateCommentRequestDto dto = new CreateCommentRequestDto();
        dto.setTaskId(999L);
        dto.setUserId(1L);
        dto.setContent("This is a new comment.");
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.addComment(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository, times(1)).findById(999L);
    }

    @Sql(scripts = "classpath:database/comment/add-test-comment.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/comment/clear-db-comment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldGetCommentsForTask() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Test Task");

        Comment comment1 = new Comment();
        comment1.setContent("First comment");
        comment1.setText("first text");
        comment1.setTask(task);

        Comment comment2 = new Comment();
        comment2.setContent("Second comment");
        comment2.setText("second text");
        comment2.setTask(task);

        List<Comment> commentList = List.of(comment1, comment2);
        Page<Comment> commentPage = new PageImpl<>(commentList);

        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findByTaskId(1L, pageable)).thenReturn(commentPage);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setContent("First comment");
        commentDto1.setText("first text");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setContent("Second comment");
        commentDto2.setText("second text");

        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        Page<CommentDto> commentDtoPage = commentService.getCommentsForTask(1L, pageable);

        assertThat(commentDtoPage).isNotNull();
        assertThat(commentDtoPage.getContent()).hasSize(2);

        CommentDto dto1 = commentDtoPage.getContent().get(0);
        assertThat(dto1.getContent()).isEqualTo("First comment");
        assertThat(dto1.getText()).isEqualTo("first text");

        CommentDto dto2 = commentDtoPage.getContent().get(1);
        assertThat(dto2.getContent()).isEqualTo("Second comment");
        assertThat(dto2.getText()).isEqualTo("second text");
    }
}
