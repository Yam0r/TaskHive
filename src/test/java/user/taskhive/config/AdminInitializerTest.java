package user.taskhive.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import my.app.files.config.AdminInitializer;
import my.app.files.model.Role;
import my.app.files.model.User;
import my.app.files.repository.RoleRepository;
import my.app.files.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {
    private static final String EMAIL = "Admin@Example.com";
    private static final String PASSWORD = "super-secret";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final ApplicationArguments args = mock(ApplicationArguments.class);

    private AdminInitializer initializer(String email, String password) {
        return new AdminInitializer(userRepository, roleRepository, passwordEncoder,
                email, password);
    }

    @Test
    void run_CredentialsNotSet_DoesNothing() {
        initializer("", "").run(args);

        verifyNoInteractions(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void run_AdminAlreadyExists_DoesNotCreateAnother() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

        initializer(EMAIL, PASSWORD).run(args);

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_NewAdmin_CreatesUserWithAdminRoleAndEncodedPassword() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        Role adminRole = new Role();
        adminRole.setRole(Role.RoleName.ADMIN);
        when(roleRepository.findByRole(Role.RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encoded");

        initializer(EMAIL, PASSWORD).run(args);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User admin = captor.getValue();
        assertThat(admin.getEmail()).isEqualTo("admin@example.com");
        assertThat(admin.getPassword()).isEqualTo("encoded");
        assertThat(admin.getRoles()).containsExactly(adminRole);
    }
}
