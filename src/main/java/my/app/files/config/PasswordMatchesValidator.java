package my.app.files.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import my.app.files.dto.user.UserRegistrationRequestDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,
        UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getRepeatPassword() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getRepeatPassword());
    }
}
