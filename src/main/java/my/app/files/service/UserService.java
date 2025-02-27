package my.app.files.service;

import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.RegistrationException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
