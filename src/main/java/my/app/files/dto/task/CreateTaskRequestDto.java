package my.app.files.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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
