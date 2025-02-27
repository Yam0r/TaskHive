package my.app.files.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Task {
    Long Id;
    Long AssigneeId;
    String name;
    String description;
    LocalDate dueDate;
}
