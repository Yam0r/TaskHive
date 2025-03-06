package my.app.files.dto.project;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProjectRequestDto {
    @Size(min = 3, max = 255, message = "Project name must be between 3 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}
