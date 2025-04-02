package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.model.User;
import my.app.files.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Manages user profiles and roles.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    @Operation(summary = "Update user role",
            description = "Allows an admin to update a user's role.")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("{id}/role")
    public void updateUserRole(@PathVariable Long id,
                               @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        userService.updateUserRole(id, requestDto);
    }

    @Operation(summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfileInfo(@AuthenticationPrincipal UserDetails
                                                                        userDetails) {
        return ResponseEntity.ok(userService.getMyProfileInfo(userDetails.getUsername()));
    }

    @Operation(summary = "Update profile",
            description = "Allows a user to update their own profile information.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/me")
    public void updateProfileInfo(@RequestBody @Valid UpdateProfileRequestDto requestDto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getId().equals(requestDto.getId())) {
            throw new AccessDeniedException("You can only edit your own profile");
        }
        userService.updateProfileInfo(user.getId(), requestDto);
    }
}
