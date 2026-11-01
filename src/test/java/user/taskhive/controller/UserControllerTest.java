package user.taskhive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import user.taskhive.config.TestDataUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {
    private static final String ROLES_OF_USER_QUERY = "SELECT r.role FROM users_roles ur "
            + "JOIN roles r ON r.id = ur.role_id WHERE ur.user_id = ?";
    private static final String NAMES_OF_USER_QUERY =
            "SELECT first_name, last_name FROM users WHERE id = ?";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateUserRole_AsAdmin_ChangesRoleInDatabase() throws Exception {
        UpdateUserRoleRequestDto requestDto = TestDataUtil
                .createTestUpdateUserRoleRequestDto(Role.RoleName.ADMIN);

        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        assertThat(rolesOfUser(1L)).containsExactly("ADMIN");
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateUserRole_AsUser_ReturnsForbiddenAndKeepsRole() throws Exception {
        UpdateUserRoleRequestDto requestDto = TestDataUtil
                .createTestUpdateUserRoleRequestDto(Role.RoleName.ADMIN);

        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        assertThat(rolesOfUser(1L)).containsExactly("USER");
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getMyProfile_ReturnsCurrentUserWithNames() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateMyProfile_OwnId_UpdatesNamesInDatabase() throws Exception {
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profileJson(1L, "Peter", "Pavel")))
                .andExpect(status().isOk());

        Map<String, Object> names = jdbcTemplate.queryForMap(NAMES_OF_USER_QUERY, 1L);
        assertThat(names).containsEntry("first_name", "Peter")
                .containsEntry("last_name", "Pavel");
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateMyProfile_OtherUsersId_ReturnsForbiddenAndChangesNothing() throws Exception {
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profileJson(2L, "Hacker", "Hacker")))
                .andExpect(status().isForbidden());

        Map<String, Object> names = jdbcTemplate.queryForMap(NAMES_OF_USER_QUERY, 2L);
        assertThat(names).containsEntry("first_name", "Admin")
                .containsEntry("last_name", "User");
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateMyProfile_TooShortName_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profileJson(1L, "P", "Pavel")))
                .andExpect(status().isBadRequest());
    }

    private List<String> rolesOfUser(Long userId) {
        return jdbcTemplate.queryForList(ROLES_OF_USER_QUERY, String.class, userId);
    }

    private String profileJson(Long id, String firstName, String lastName) throws Exception {
        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto();
        requestDto.setId(id);
        requestDto.setFirstName(firstName);
        requestDto.setLastName(lastName);
        return objectMapper.writeValueAsString(requestDto);
    }
}
