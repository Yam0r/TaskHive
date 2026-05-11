package my.app.files.mapper;

import my.app.files.config.MapperConfig;
import my.app.files.dto.project.CreateProjectRequestDto;
import my.app.files.dto.project.ProjectDto;
import my.app.files.dto.project.UpdateProjectRequestDto;
import my.app.files.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    ProjectDto toProjectDto(Project project);

    @Mapping(target = "id", ignore = true)
    Project toProject(CreateProjectRequestDto requestDto);

    void updateProjectFromDto(UpdateProjectRequestDto requestDto, @MappingTarget Project project);
}
