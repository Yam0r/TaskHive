package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.dropbox.core.v2.DbxClientV2;
import java.util.Arrays;
import java.util.List;
import my.app.files.dto.attachment.AttachmentDto;
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

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DbxClientV2 dropboxClient;

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

        Mockito.when(attachmentRepository.findByTaskId(taskId)).thenReturn(attachments);

        List<AttachmentDto> attachmentDtos = attachmentService.getAttachmentsForTask(taskId);

        assertThat(attachmentDtos).hasSize(2);
        assertThat(attachmentDtos.get(0).getFilename()).isEqualTo("test-file-1.txt");
        assertThat(attachmentDtos.get(1).getFilename()).isEqualTo("test-file-2.txt");
    }
}
