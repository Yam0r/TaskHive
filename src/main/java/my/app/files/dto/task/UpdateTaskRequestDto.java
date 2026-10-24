package my.app.files.dto.task;

import lombok.Getter;
import lombok.Setter;
import my.app.files.model.TaskStatus;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTaskRequestDto {
    private String name;
    private String description;
    private LocalDate dueDate;
    private Long assigneeId;

    private TaskStatus status;
}
