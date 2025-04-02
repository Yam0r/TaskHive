package my.app.files.service;

import java.util.List;
import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;

public interface CommentService {
    CommentDto addComment(CreateCommentRequestDto dto);

    List<CommentDto> getCommentsForTask(Long taskId);
}
