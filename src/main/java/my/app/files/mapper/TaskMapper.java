package my.app.files.mapper;

import my.app.files.dto.task.CreateTaskRequestDto;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.task.UpdateTaskRequestDto;
import my.app.files.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task toEntity(CreateTaskRequestDto dto);

    void updateTaskFromDto(UpdateTaskRequestDto dto, @MappingTarget Task task);
}
