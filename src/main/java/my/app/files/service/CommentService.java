package my.app.files.service;

import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto addComment(CreateCommentRequestDto dto);

    Page<CommentDto> getCommentsForTask(Long taskId, Pageable pageable);
}
