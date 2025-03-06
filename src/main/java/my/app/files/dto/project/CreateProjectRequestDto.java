package my.app.files.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateProjectRequestDto {
    @NotBlank(message = "Project name cannot be blank")
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}
