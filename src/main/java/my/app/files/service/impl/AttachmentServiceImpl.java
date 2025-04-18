package my.app.files.service.impl;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.time.LocalDateTime;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.mapper.AttachmentMapper;
import my.app.files.model.Attachment;
import my.app.files.model.Task;
import my.app.files.repository.AttachmentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DbxClientV2 dropboxClient;
    private final AttachmentMapper attachmentMapper;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 TaskRepository taskRepository,
                                 AttachmentMapper attachmentMapper) {
        Dotenv dotenv = Dotenv.load();
        String appName = dotenv.get("MY_APP_NAME");
        String accessToken = dotenv.get("MY_DROPBOX_ACCESS_TOKEN");

        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
        this.dropboxClient = new DbxClientV2(config, accessToken);
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    @Transactional
    public AttachmentDto uploadAttachment(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        try (InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder("/" + file.getOriginalFilename())
                    .uploadAndFinish(inputStream);

            AttachmentDto data = new AttachmentDto(
                    null,
                    file.getOriginalFilename(),
                    metadata.getId(),
                    LocalDateTime.now()
            );

            Attachment attachment = attachmentMapper.toEntity(data);
            Attachment savedAttachment = attachmentRepository.save(attachment);

            return attachmentMapper.toDto(savedAttachment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public Page<AttachmentDto> getAttachmentsForTask(Long taskId, Pageable pageable) {
        return attachmentRepository.findByTaskId(taskId, pageable)
                .map(attachmentMapper::toDto);
    }
}

