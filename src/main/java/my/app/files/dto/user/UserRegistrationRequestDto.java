package my.app.files.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import my.app.files.validator.FieldMatch;

@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords do not match")
@Getter
@Setter
public class UserRegistrationRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 35, message = "Password must be between 8 and 35 characters")
    private String password;

    @NotBlank(message = "Repeat password is required")
    @Size(min = 8, max = 35, message = "Repeat password must be between 8 and 35 characters")
    private String repeatPassword;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
}
