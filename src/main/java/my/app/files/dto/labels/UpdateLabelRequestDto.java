package my.app.files.dto.labels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLabelRequestDto {
    private String name;
    private String color;
}
