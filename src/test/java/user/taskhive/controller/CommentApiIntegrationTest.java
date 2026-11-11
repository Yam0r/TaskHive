package user.taskhive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CommentApiIntegrationTest {
    private static final String OWNER = "testuser@example.com";
    private static final String STRANGER = "admin@example.com";
    private static final String COUNT_COMMENTS_QUERY = "SELECT COUNT(*) FROM comments";
    private static final String AUTHOR_BY_TEXT_QUERY =
            "SELECT user_id FROM comments WHERE text = ?";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = OWNER, roles = "USER")
    void getComments_OwnTask_ReturnsComments() throws Exception {
        mockMvc.perform(get("/comments").param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void getComments_OfAnotherUsersTask_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/comments").param("taskId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = STRANGER, roles = "USER")
    void addComment_ToAnotherUsersTask_ReturnsNotFoundAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": 1, \"text\": \"Sneaky\"}"))
                .andExpect(status().isNotFound());

        assertThat(jdbcTemplate.queryForObject(COUNT_COMMENTS_QUERY, Integer.class))
                .isEqualTo(2);
    }

    @Test
    @WithMockUser(username = OWNER, roles = "USER")
    void addComment_AuthorIsTakenFromToken_NotFromRequestBody() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\": 1, \"userId\": 2, \"text\": \"Hello\"}"))
                .andExpect(status().isCreated());

        Long authorId = jdbcTemplate.queryForObject(AUTHOR_BY_TEXT_QUERY, Long.class, "Hello");
        assertThat(authorId).isEqualTo(1L);
    }
}
