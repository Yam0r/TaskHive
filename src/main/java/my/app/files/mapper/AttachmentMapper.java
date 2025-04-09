package my.app.files.mapper;

import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDto toDto(Attachment attachment);

    @Mapping(target = "id", ignore = true)
    Attachment toEntity(AttachmentDto dto);
}

