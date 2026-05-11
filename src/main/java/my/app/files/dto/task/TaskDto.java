package my.app.files.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private Long assigneeId;

    @NotBlank(message = "Title must not be empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String name;

    private String description;
    private LocalDate dueDate;
}
