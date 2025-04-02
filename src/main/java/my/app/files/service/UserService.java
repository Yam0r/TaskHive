package my.app.files.service;

import java.util.Optional;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.RegistrationException;
import my.app.files.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    void updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto);

    UserResponseDto getMyProfileInfo(String email);

    void updateProfileInfo(Long userId, UpdateProfileRequestDto requestDto);

    Optional<User> findByEmail(String email);
}

