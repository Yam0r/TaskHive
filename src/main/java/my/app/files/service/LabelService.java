package my.app.files.service;

import java.util.List;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;

public interface LabelService {
    LabelDto createLabel(CreateLabelRequestDto dto);

    List<LabelDto> getAllLabels();

    LabelDto updateLabel(Long id, UpdateLabelRequestDto dto);

    void deleteLabel(Long id);
}
