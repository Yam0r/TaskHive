package my.app.files.mapper;

import my.app.files.config.MapperConfig;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper{
    UserResponseDto toUserResponse(User savedUser);

    @Mapping(target = "username", source = "username")
    User toUser(UserRegistrationRequestDto request);
}
