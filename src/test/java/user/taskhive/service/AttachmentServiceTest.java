package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;

import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.model.Attachment;
import my.app.files.repository.AttachmentRepository;
import my.app.files.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "classpath:database/attachment/clear-db-for-att.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/attachment/add-test-att.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/attachment/clear-db-for-att.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AttachmentServiceTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Test
    void shouldGetAttachmentsForTask() {
        Long taskId = 1L;

        Page<Attachment> attachmentPage = attachmentRepository.findByTaskId(taskId,
                Pageable.unpaged());

        Page<AttachmentDto> attachmentDtos = attachmentService
                .getAttachmentsForTask(taskId, Pageable.unpaged());

        assertThat(attachmentDtos.getContent()).isNotNull();
        assertThat(attachmentDtos.getContent()).hasSize(1);
        assertThat(attachmentDtos.getContent().get(0).getFilename()).isEqualTo("test-file.txt");
        assertThat(attachmentDtos.getContent().get(0).getId()).isEqualTo(1L);
    }
}
