package my.app.files.service;

import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(CreateCommentRequestDto dto);

    List<CommentDto> getCommentsForTask(Long taskId);
}
