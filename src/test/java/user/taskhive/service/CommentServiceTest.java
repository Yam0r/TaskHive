package user.taskhive.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

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

        Task task = new Task();
        task.setId(1L);

        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setContent("This is a new comment.");
        comment.setTask(task);
        comment.setAuthor(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a new comment.");

        Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(commentMapper.toEntity(dto)).thenReturn(comment);
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto addedComment = commentService.addComment(dto);

        assertThat(addedComment).isNotNull();
        assertThat(addedComment.getContent()).isEqualTo("This is a new comment.");

        Mockito.verify(taskRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        CreateCommentRequestDto dto = new CreateCommentRequestDto();
        dto.setTaskId(999L);
        dto.setUserId(1L);
        dto.setContent("This is a new comment.");

        Mockito.when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            commentService.addComment(dto);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EntityNotFoundException.class);
        }

        Mockito.verify(taskRepository, Mockito.times(1)).findById(999L);
    }

    @Sql(scripts = "classpath:database/comment/add-test-comment.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/comment/clear-db-comment.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldGetCommentsForTask() {
        Task task = new Task();
        task.setId(1L);

        Comment comment1 = new Comment();
        comment1.setContent("First comment");
        comment1.setText("first text");
        comment1.setTask(task);

        Comment comment2 = new Comment();
        comment2.setContent("Second comment");
        comment2.setText("second text");
        comment2.setTask(task);

        List<Comment> comments = List.of(comment1, comment2);
        Mockito.when(commentRepository.findByTaskId(1L)).thenReturn(comments);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setContent("First comment");
        commentDto1.setText("first text");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setContent("Second comment");
        commentDto2.setText("second text");

        Mockito.when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        Mockito.when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        List<CommentDto> commentDtos = commentService.getCommentsForTask(1L);

        assertThat(commentDtos).isNotEmpty();
        assertThat(commentDtos.size()).isEqualTo(2);

        CommentDto commentDtoResult1 = commentDtos.get(0);
        assertThat(commentDtoResult1.getContent()).isEqualTo("First comment");
        assertThat(commentDtoResult1.getText()).isEqualTo("first text");

        CommentDto commentDtoResult2 = commentDtos.get(1);
        assertThat(commentDtoResult2.getContent()).isEqualTo("Second comment");
        assertThat(commentDtoResult2.getText()).isEqualTo("second text");
    }
}
