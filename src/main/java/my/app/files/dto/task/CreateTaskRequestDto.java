package my.app.files.dto.task;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequestDto {
    private String name;
    private String description;
    private LocalDate dueDate;
    private Long assigneeId;
    private Long projectId;
    private List<Long> labelIds;
}
