package my.app.files.dto.labels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLabelRequestDto {
    private String name;
    private String color;
}
