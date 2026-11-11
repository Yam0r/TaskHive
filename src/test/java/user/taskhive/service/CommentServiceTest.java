package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final String USER_EMAIL = "author@example.com";

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
    void addComment_OwnTask_SavesCommentWithAuthorFromToken() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(task));
        User author = TestDataUtil.createTestUser(2L, USER_EMAIL, "John", "Doe");
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        CommentDto commentDto = new CommentDto();
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.addComment(createRequest(1L), USER_EMAIL);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment savedComment = captor.getValue();
        assertThat(savedComment.getTask()).isSameAs(task);
        assertThat(savedComment.getAuthor()).isSameAs(author);
        assertThat(savedComment.getText()).isEqualTo("Comment text");
        assertThat(savedComment.getContent()).isEqualTo("Comment content");
        assertThat(result).isSameAs(commentDto);
    }

    @Test
    void addComment_TaskNotFoundOrNotOwned_ThrowsAndDoesNotSave() {
        when(taskRepository.findByIdAndProjectUserEmail(999L, USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(createRequest(999L), USER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Task not found");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_AuthorNotFound_ThrowsAndDoesNotSave() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(task));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(createRequest(1L), USER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsForTask_OwnTask_MapsEveryComment() {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findByIdAndProjectUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(task));
        Comment first = new Comment();
        Comment second = new Comment();
        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findByTaskId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(first, second)));
        CommentDto firstDto = new CommentDto();
        when(commentMapper.toDto(first)).thenReturn(firstDto);
        CommentDto secondDto = new CommentDto();
        when(commentMapper.toDto(second)).thenReturn(secondDto);

        Page<CommentDto> result = commentService.getCommentsForTask(1L, pageable, USER_EMAIL);

        assertThat(result.getContent()).containsExactly(firstDto, secondDto);
    }

    @Test
    void getCommentsForTask_NotOwnedTask_ThrowsAndDoesNotLoadComments() {
        when(taskRepository.findByIdAndProjectUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> commentService.getCommentsForTask(1L, pageable, USER_EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
        verify(commentRepository, never()).findByTaskId(any(), any());
    }

    private CreateCommentRequestDto createRequest(Long taskId) {
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto();
        requestDto.setTaskId(taskId);
        requestDto.setText("Comment text");
        requestDto.setContent("Comment content");
        return requestDto;
    }
}
