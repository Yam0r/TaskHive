package user.taskhive.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import my.app.files.model.Attachment;
import my.app.files.repository.AttachmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:database/clear-all.sql", "classpath:database/test-data.sql"})
class AttachmentRepositoryTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    void findByTaskId_TaskWithAttachment_ReturnsIt() {
        List<Attachment> attachments = attachmentRepository
                .findByTaskId(1L, PageRequest.of(0, 10))
                .getContent();

        assertThat(attachments).hasSize(1);
        Attachment attachment = attachments.get(0);
        assertThat(attachment.getFilename()).isEqualTo("test-file.txt");
        assertThat(attachment.getDropboxFileId()).isEqualTo("dropbox-id-1");
        assertThat(attachment.getUploadDate()).isNotNull();
    }

    @Test
    void findByTaskId_UnknownTask_ReturnsEmptyPage() {
        assertThat(attachmentRepository.findByTaskId(999L, PageRequest.of(0, 10))).isEmpty();
    }
}
