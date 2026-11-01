package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserRegistrationRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.exception.EntityNotFoundException;
import my.app.files.exception.UserAlreadyExistsException;
import my.app.files.mapper.UserMapper;
import my.app.files.model.Role;
import my.app.files.model.User;
import my.app.files.repository.RoleRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_NewUser_SavesLowercaseEmailHashedPasswordAndUserRole() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        Role userRole = TestDataUtil.createTestRole(1L, Role.RoleName.USER);
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        UserRegistrationRequestDto requestDto = createRegistrationRequest("NewUser@Example.com");
        when(userMapper.toUser(requestDto)).thenReturn(new User());
        when(passwordEncoder.encode("12345678")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        UserResponseDto responseDto = new UserResponseDto();
        when(userMapper.toUserResponse(any(User.class))).thenReturn(responseDto);

        UserResponseDto result = userService.register(requestDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
        assertThat(savedUser.getRoles()).containsExactly(userRole);
        assertThat(result).isSameAs(responseDto);
    }

    @Test
    void register_EmailAlreadyExists_ThrowsAndDoesNotSave() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                createRegistrationRequest("Existing@Example.com")))
                .isInstanceOf(UserAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUserRole_ExistingUser_ReplacesRoles() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Role adminRole = TestDataUtil.createTestRole(2L, Role.RoleName.ADMIN);
        when(roleRepository.findByRole(Role.RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        UpdateUserRoleRequestDto requestDto = TestDataUtil
                .createTestUpdateUserRoleRequestDto(Role.RoleName.ADMIN);

        userService.updateUserRole(1L, requestDto);

        assertThat(user.getRoles()).containsExactly(adminRole);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_UnknownUser_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        UpdateUserRoleRequestDto requestDto = TestDataUtil
                .createTestUpdateUserRoleRequestDto(Role.RoleName.ADMIN);

        assertThatThrownBy(() -> userService.updateUserRole(99L, requestDto))
                .isInstanceOf(EntityNotFoundException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfileInfo_ChangesFirstAndLastName() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UpdateProfileRequestDto requestDto = TestDataUtil
                .createTestUpdateProfileRequestDto(1L, "UpdatedName", "UpdatedLastName");

        userService.updateProfileInfo(1L, requestDto);

        assertThat(user.getFirstName()).isEqualTo("UpdatedName");
        assertThat(user.getLastName()).isEqualTo("UpdatedLastName");
        verify(userRepository).save(user);
    }

    @Test
    void getMyProfileInfo_ExistingUser_ReturnsMappedUser() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        UserResponseDto responseDto = new UserResponseDto();
        when(userMapper.toUserResponse(user)).thenReturn(responseDto);

        assertThat(userService.getMyProfileInfo("testuser@example.com")).isSameAs(responseDto);
    }

    @Test
    void getMyProfileInfo_UnknownUser_Throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyProfileInfo("missing@example.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private UserRegistrationRequestDto createRegistrationRequest(String email) {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword("12345678");
        requestDto.setRepeatPassword("12345678");
        requestDto.setUsername("NewUser");
        requestDto.setFirstName("Peter");
        requestDto.setLastName("Pavel");
        return requestDto;
    }
}
