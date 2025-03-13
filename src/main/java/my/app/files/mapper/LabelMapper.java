package my.app.files.mapper;

import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    Label toEntity(CreateLabelRequestDto dto);

    LabelDto toDto(Label label);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Label label, UpdateLabelRequestDto dto);
}
