package my.app.files.dto.task;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequestDto {
    private String name;
    private String description;
    private LocalDate dueDate;
    private Long assigneeId;
}
