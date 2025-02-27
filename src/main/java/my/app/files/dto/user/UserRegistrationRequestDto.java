package my.app.files.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserRegistrationRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 35)
    private String password;

    @NotBlank
    @Length(min = 8, max = 35)
    private String repeatPassword;

    @NotBlank
    private String username;

    private String firstName;

    private String lastName;

    private String shippingAddress;
}
