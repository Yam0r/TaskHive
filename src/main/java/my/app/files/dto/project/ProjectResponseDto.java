package my.app.files.dto.project;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponseDto {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
