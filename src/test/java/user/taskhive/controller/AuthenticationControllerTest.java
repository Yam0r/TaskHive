package user.taskhive.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationControllerTest {
    private static final String PASSWORD = "12345678";
    private static final String NEW_USER_EMAIL = "newuser@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ValidRequest_SavesNamesAndLowercaseEmail() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson("NewUser@Example.com", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(NEW_USER_EMAIL))
                .andExpect(jsonPath("$.firstName").value("Peter"))
                .andExpect(jsonPath("$.lastName").value("Pavel"));
    }

    @Test
    void register_EmailExistsInOtherCase_ReturnsConflict() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson("TestUser@Example.com", PASSWORD)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_PasswordsDoNotMatch_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson(NEW_USER_EMAIL, "otherPassword")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_AfterRegistration_ReturnsTokenThatGivesAccess() throws Exception {
        registerNewUser();

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(NEW_USER_EMAIL));
    }

    @Test
    void login_WrongPassword_ReturnsUnauthorized() throws Exception {
        registerNewUser();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("wrongPassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    private void registerNewUser() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson(NEW_USER_EMAIL, PASSWORD)))
                .andExpect(status().isOk());
    }

    private String registrationJson(String email, String repeatPassword)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", PASSWORD,
                "repeatPassword", repeatPassword,
                "username", "PetPav",
                "firstName", "Peter",
                "lastName", "Pavel"));
    }

    private String loginJson(String password) throws JsonProcessingException {
        return objectMapper.writeValueAsString(Map.of(
                "email", NEW_USER_EMAIL,
                "password", password));
    }
}
