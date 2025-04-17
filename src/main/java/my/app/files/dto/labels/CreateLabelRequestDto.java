package my.app.files.dto.labels;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLabelRequestDto {
    @NotBlank(message = "Label name cannot be blank")
    private String name;

    private String color;
}
