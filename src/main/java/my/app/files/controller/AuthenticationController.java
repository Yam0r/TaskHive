package my.app.files.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.user.UserLoginRequestDto;
import my.app.files.dto.user.UserLoginResponseDto;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.RegistrationException;
import my.app.files.security.AuthenticationService;
import my.app.files.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/registration")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
