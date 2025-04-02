package my.app.files.dto.comment;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
