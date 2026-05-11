package my.app.files.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequestDto {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Author ID is required")
    private Long userId;

    @NotBlank(message = "Text must not be empty")
    @Size(max = 500, message = "Comment text must be at most 500 characters")
    private String text;

    private String content;
}
