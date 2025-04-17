package user.taskhive.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.mapper.UserMapper;
import my.app.files.model.Role;
import my.app.files.model.User;
import my.app.files.repository.RoleRepository;
import my.app.files.repository.UserRepository;
import my.app.files.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock(lenient = true)
    private RoleRepository roleRepository;

    @Mock(lenient = true)
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Sql(scripts = "classpath:database/user/add-test-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/user/clear-db-for-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testUpdateUserRole_ShouldUpdateRole() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");
        Role newRole = TestDataUtil.createTestRole(2L, Role.RoleName.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(Role.RoleName.ADMIN)).thenReturn(Optional.of(newRole));

        UpdateUserRoleRequestDto requestDto = TestDataUtil
                .createTestUpdateUserRoleRequestDto(Role.RoleName.ADMIN);

        userService.updateUserRole(1L, requestDto);

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(newRole));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateProfileInfo_ShouldUpdateUserProfile() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");

        UpdateProfileRequestDto requestDto = TestDataUtil
                .createTestUpdateProfileRequestDto(1L, "UpdatedName", "UpdatedLastName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateProfileInfo(1L, requestDto);

        assertEquals("UpdatedName", user.getFirstName());
        assertEquals("UpdatedLastName", user.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetMyProfileInfo_ShouldReturnUserResponseDto() {
        User user = TestDataUtil.createTestUser(1L, "testuser@example.com", "John", "Doe");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(new UserResponseDto());

        UserResponseDto responseDto = userService.getMyProfileInfo("testuser@example.com");

        assertNotNull(responseDto);
        verify(userRepository, times(1)).findByEmail("testuser@example.com");
    }
}
