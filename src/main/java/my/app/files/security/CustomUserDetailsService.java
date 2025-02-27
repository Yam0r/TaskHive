package my.app.files.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.model.Role;
import my.app.files.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import my.app.files.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String NOT_FOUND_EMAIL_MESSAGE = "Can't find user with email: ";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EMAIL_MESSAGE + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(Role::getAuthority)
                        .toArray(String[]::new))
                .build();
    }
}
