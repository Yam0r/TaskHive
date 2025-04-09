package my.app.files.security;

import lombok.RequiredArgsConstructor;
import my.app.files.model.User;
import my.app.files.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String NOT_FOUND_EMAIL_MESSAGE = "Can't find user with email: ";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_EMAIL_MESSAGE + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().stream()
                        .map(role -> role.getRole().name())
                        .toArray(String[]::new))
                .build();
    }
}
