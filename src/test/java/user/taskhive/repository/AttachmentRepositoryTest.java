package user.taskhive.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import my.app.files.model.Attachment;
import my.app.files.model.Project;
import my.app.files.model.Task;
import my.app.files.repository.AttachmentRepository;
import my.app.files.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import user.taskhive.config.TestDataUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
public class AttachmentRepositoryTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Sql(scripts = "classpath:database/attachment/add-test-att.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/attachment/clear-db-for-att.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldFindAttachmentsByTaskId() {
        Project project = TestDataUtil.createTestProject(2L);
        Task task = TestDataUtil.createTestTask(project, "Test Task");
        taskRepository.save(task);

        Attachment attachment = TestDataUtil.createTestAttachment(task, "test-file.txt",
                "dropbox-file-id");
        attachmentRepository.save(attachment);

        Pageable pageable = PageRequest.of(0, 10);
        List<Attachment> attachments = attachmentRepository.findByTaskId(task.getId(), pageable)
                .getContent();

        assertThat(attachments).isNotEmpty();
        assertThat(attachments.size()).isEqualTo(1);
        assertThat(attachments.get(0).getFilename()).isEqualTo("test-file.txt");
        assertThat(attachments.get(0).getDropboxFileId()).isEqualTo("dropbox-file-id");
        assertThat(attachments.get(0).getUploadDate()).isNotNull();
    }

    @Test
    void shouldReturnEmptyListIfNoAttachmentsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Attachment> attachments = attachmentRepository.findByTaskId(999L, pageable)
                .getContent();

        assertThat(attachments).isEmpty();
    }
}
