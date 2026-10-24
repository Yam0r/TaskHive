package my.app.files.dto.task;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatisticsDto{
    private Long totalTasks;
    private Long notStartedTasks;
    private Long inProgressTasks;
    private Long completedTasks;
}
