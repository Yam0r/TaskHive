package my.app.files.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserLoginRequestDto {
    @NotBlank
    @Length(min = 8, max = 35)
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 35)
    private String password;
}
