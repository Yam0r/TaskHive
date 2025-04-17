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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        dto.setText("Optional text");

        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "Test", "User");
        Task task = TestDataUtil.createTestTask(TestDataUtil
                .createTestProject(2L), "Task for Testing");

        Comment commentWithoutId = new Comment();
        commentWithoutId.setContent(dto.getContent());
        commentWithoutId.setText("Optional text");
        commentWithoutId.setTask(task);
        commentWithoutId.setAuthor(user);

        Comment savedComment = new Comment();
        savedComment.setId(10L);
        savedComment.setContent(dto.getContent());
        savedComment.setText("Optional text");
        savedComment.setTask(task);
        savedComment.setAuthor(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(10L);
        commentDto.setContent(dto.getContent());
        commentDto.setText("Optional text");
        commentDto.setTaskId(1L);
        commentDto.setUserId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(dto)).thenReturn(commentWithoutId);
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(commentDto);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

        CommentDto addedComment = commentService.addComment(dto);

        assertThat(addedComment).isNotNull();
        assertThat(addedComment.getId()).isEqualTo(10L);
        assertThat(addedComment.getContent()).isEqualTo(dto.getContent());
        assertThat(addedComment.getUserId()).isEqualTo(1L);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();

        assertThat(capturedComment).isEqualToIgnoringGivenFields(commentWithoutId, "id");
        assertThat(capturedComment.getText()).isEqualTo("Optional text");
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

    @Test
    void shouldGetCommentsForTask() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("TestUser");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Test2User");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("First comment");
        comment1.setText("first text");
        comment1.setAuthor(user1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Second comment");
        comment2.setText("second text");
        comment2.setAuthor(user2);

        Task task = TestDataUtil.createTestTask(TestDataUtil
                .createTestProject(2L), "Task for Testing");
        comment1.setTask(task);
        comment2.setTask(task);

        List<Comment> commentList = List.of(comment1, comment2);
        Page<Comment> commentPage = new PageImpl<>(commentList);
        Pageable pageable = PageRequest.of(0, 10);

        when(commentRepository.findByTaskId(1L, pageable)).thenReturn(commentPage);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setContent("First comment");
        commentDto1.setText("first text");
        commentDto1.setUserId(1L);
        commentDto1.setTaskId(1L);

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(2L);
        commentDto2.setContent("Second comment");
        commentDto2.setText("second text");
        commentDto2.setUserId(2L);
        commentDto2.setTaskId(1L);

        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        Page<CommentDto> result = commentService.getCommentsForTask(1L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("First comment");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("Second comment");

        verify(commentRepository, times(1)).findByTaskId(1L, pageable);
    }
}
