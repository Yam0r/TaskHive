package my.app.files.mapper;

import my.app.files.config.MapperConfig;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRegistrationRequestDto request);
}
