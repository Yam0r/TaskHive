package my.app.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;
import my.app.files.mapper.CommentMapper;
import my.app.files.model.Comment;
import my.app.files.model.Task;
import my.app.files.model.User;
import my.app.files.repository.CommentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(CreateCommentRequestDto dto) {
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User author = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = commentMapper.toEntity(dto);
        comment.setTask(task);
        comment.setAuthor(author);

        comment.setContent(dto.getContent() != null ? dto.getContent() : "");

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsForTask(Long taskId){
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }
}
