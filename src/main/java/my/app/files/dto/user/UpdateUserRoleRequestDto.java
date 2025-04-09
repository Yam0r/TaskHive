package my.app.files.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import my.app.files.model.Role.RoleName;

@Getter
@Setter
public class UpdateUserRoleRequestDto {
    @NotNull(message = "New role is required")
    private RoleName newRole;
}
