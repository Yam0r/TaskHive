package my.app.files.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.EntityNotFoundException;
import my.app.files.exception.RegistrationException;
import my.app.files.exception.UserAlreadyExistsException;
import my.app.files.mapper.UserMapper;
import my.app.files.model.Role;
import my.app.files.model.User;
import my.app.files.repository.RoleRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String NOT_REGISTRATION_EMAIL_MESSAGE = "Can't register user: "
            + "email already exists";
    private static final String NOT_FOUND_ROLE = "Role %s not found in the database:";
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws UserAlreadyExistsException, RegistrationException {

        String email = requestDto.getEmail().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(NOT_REGISTRATION_EMAIL_MESSAGE);
        }

        String roleName = Role.RoleName.USER.name();
        Role userRole = roleRepository.findByRole(Role.RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_ROLE,
                        roleName)));

        User user = userMapper.toUser(requestDto);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(userRole));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: "
                        + userId));

        Role role = roleRepository.findByRole(requestDto.getNewRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                        + requestDto.getNewRole()));

        user.setRoles(new HashSet<>(Set.of(role)));
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getMyProfileInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void updateProfileInfo(Long userId, UpdateProfileRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: "
                        + userId));

        Optional.ofNullable(dto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(dto.getLastName()).ifPresent(user::setLastName);

        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
