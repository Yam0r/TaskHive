package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.mapper.AttachmentMapper;
import my.app.files.model.Attachment;
import my.app.files.model.Task;
import my.app.files.repository.AttachmentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {
    private static final MockMultipartFile FILE = new MockMultipartFile(
            "file", "report.pdf", "application/pdf", "file content".getBytes());

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DbxClientV2 dropboxClient;

    @Mock
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Test
    void uploadAttachment_ExistingTask_UploadsFileAndSavesAttachmentOfTask() throws Exception {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        FileMetadata metadata = mock(FileMetadata.class);
        when(metadata.getId()).thenReturn("id:abc");
        when(dropboxClient.files().uploadBuilder("/report.pdf")
                .uploadAndFinish(any(InputStream.class)))
                .thenReturn(metadata);
        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AttachmentDto attachmentDto = new AttachmentDto();
        when(attachmentMapper.toDto(any(Attachment.class))).thenReturn(attachmentDto);

        AttachmentDto result = attachmentService.uploadAttachment(1L, FILE);

        ArgumentCaptor<Attachment> captor = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepository).save(captor.capture());
        Attachment savedAttachment = captor.getValue();
        assertThat(savedAttachment.getTask()).isSameAs(task);
        assertThat(savedAttachment.getFilename()).isEqualTo("report.pdf");
        assertThat(savedAttachment.getDropboxFileId()).isEqualTo("id:abc");
        assertThat(savedAttachment.getUploadDate()).isNotNull();
        assertThat(result).isSameAs(attachmentDto);
    }

    @Test
    void uploadAttachment_UnknownTask_ThrowsWithoutUploading() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.uploadAttachment(99L, FILE))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Task not found");
        verifyNoInteractions(dropboxClient);
        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadAttachment_DropboxFails_ThrowsAndDoesNotSave() throws Exception {
        Task task = TestDataUtil.createTestTask(TestDataUtil.createTestProject(1L), "Task");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(dropboxClient.files().uploadBuilder("/report.pdf")
                .uploadAndFinish(any(InputStream.class)))
                .thenThrow(new IOException("Network is down"));

        assertThatThrownBy(() -> attachmentService.uploadAttachment(1L, FILE))
                .hasMessage("Failed to upload file")
                .hasCauseInstanceOf(IOException.class);
        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void getAttachmentsForTask_MapsEveryAttachment() {
        Attachment attachment = new Attachment();
        PageRequest pageable = PageRequest.of(0, 10);
        when(attachmentRepository.findByTaskId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(attachment)));
        AttachmentDto attachmentDto = new AttachmentDto();
        when(attachmentMapper.toDto(attachment)).thenReturn(attachmentDto);

        Page<AttachmentDto> result = attachmentService.getAttachmentsForTask(1L, pageable);

        assertThat(result.getContent()).containsExactly(attachmentDto);
    }
}
