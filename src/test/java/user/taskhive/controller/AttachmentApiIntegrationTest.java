package user.taskhive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AttachmentApiIntegrationTest {
    private static final String OWNER = "testuser@example.com";
    private static final String STRANGER = "admin@example.com";
    private static final String COUNT_ATTACHMENTS_QUERY = "SELECT COUNT(*) FROM attachments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = OWNER, roles = "USER")
    void getAttachments_OwnTask_ReturnsAttachments() throws Exception {
        mockMvc.perform(get("/attachments").param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].filename").value("test-file.txt"));
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void getAttachments_OfAnotherUsersTask_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/attachments").param("taskId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void uploadAttachment_ToAnotherUsersTask_ReturnsNotFoundAndDoesNotSave() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "evil.txt", "text/plain", "hello".getBytes());

        mockMvc.perform(multipart("/attachments").file(file).param("taskId", "1"))
                .andExpect(status().isNotFound());

        assertThat(jdbcTemplate.queryForObject(COUNT_ATTACHMENTS_QUERY, Integer.class))
                .isEqualTo(1);
    }
}
