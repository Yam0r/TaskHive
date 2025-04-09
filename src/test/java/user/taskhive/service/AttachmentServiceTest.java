package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.dropbox.core.v2.DbxClientV2;
import java.util.Arrays;
import java.util.List;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.mapper.AttachmentMapper;
import my.app.files.model.Attachment;
import my.app.files.model.Task;
import my.app.files.repository.AttachmentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DbxClientV2 dropboxClient;

    @Mock
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Test
    void shouldGetAttachmentsForTask() {
        Long taskId = 1L;

        Task task = new Task();
        task.setId(taskId);
        task.setName("Test Task");

        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachment1.setFilename("test-file-1.txt");
        attachment1.setTask(task);

        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);
        attachment2.setFilename("test-file-2.txt");
        attachment2.setTask(task);

        List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
        Page<Attachment> attachmentPage = new PageImpl<>(attachments);

        Mockito.when(attachmentRepository.findByTaskId(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(attachmentPage);

        Mockito.when(attachmentMapper.toDto(Mockito.any(Attachment.class)))
                .thenAnswer(invocation -> {
                    Attachment attachment = invocation.getArgument(0);
                    AttachmentDto dto = new AttachmentDto(); // без параметров
                    dto.setId(attachment.getId());
                    dto.setFilename(attachment.getFilename());
                    dto.setDropboxFileId(attachment.getDropboxFileId());
                    dto.setUploadDate(attachment.getUploadDate());
                    return dto;
                });

        Page<AttachmentDto> attachmentDtos = attachmentService.getAttachmentsForTask(taskId,
                Pageable.unpaged());

        assertThat(attachmentDtos.getContent()).hasSize(2);
        assertThat(attachmentDtos.getContent().get(0).getFilename()).isEqualTo("test-file-1.txt");
        assertThat(attachmentDtos.getContent().get(1).getFilename()).isEqualTo("test-file-2.txt");
        assertThat(attachmentDtos.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(attachmentDtos.getContent().get(1).getId()).isEqualTo(2L);
    }
}
