package my.app.files.service.impl;

import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.RegistrationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.mapper.UserMapper;
import my.app.files.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import my.app.files.repository.UserRepository;
import my.app.files.model.Role;
import my.app.files.rolerepository.RoleRepository;
import my.app.files.service.UserService;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
    private static final String NOT_REGISTRATION_EMAIL_MESSAGE = "Can't register user: email already exists";
    private static final String NOT_FOUND_ROLE = "Role %s not found in the database:";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(NOT_REGISTRATION_EMAIL_MESSAGE);
        }

        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        String roleName = Role.RoleName.USER.name();
        Role userRole = roleRepository.findByRole(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_ROLE, roleName)));

        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

}
