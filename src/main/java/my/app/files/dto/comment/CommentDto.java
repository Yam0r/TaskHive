package my.app.files.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private Long taskId;
    private Long userId;
    private String text;
    private String content;
    private LocalDateTime timestamp;
}
