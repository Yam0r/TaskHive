package my.app.files.config;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import my.app.files.model.Role;
import my.app.files.model.User;
import my.app.files.repository.RoleRepository;
import my.app.files.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.email:}") String adminEmail,
                            @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("ADMIN_EMAIL or ADMIN_PASSWORD is not set, skipping admin creation");
            return;
        }
        String email = adminEmail.toLowerCase();
        if (userRepository.existsByEmail(email)) {
            log.info("Admin account {} already exists", email);
            return;
        }
        Role adminRole = roleRepository.findByRole(Role.RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Role ADMIN not found"));

        User admin = new User();
        admin.setEmail(email);
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);
        log.info("Admin account {} created", email);
    }
}
