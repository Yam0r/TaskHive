package my.app.files.service.impl;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.model.Attachment;
import my.app.files.model.Task;
import my.app.files.repository.AttachmentRepository;
import my.app.files.repository.TaskRepository;
import my.app.files.service.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DbxClientV2 dropboxClient;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 TaskRepository taskRepository) {
        Dotenv dotenv = Dotenv.load();
        String appName = dotenv.get("MY_APP_NAME");
        String accessToken = dotenv.get("MY_DROPBOX_ACCESS_TOKEN");

        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
        this.dropboxClient = new DbxClientV2(config, accessToken);

        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public AttachmentDto uploadAttachment(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        try (InputStream inputStream = file.getInputStream()) {
            UploadBuilder uploadBuilder = dropboxClient.files()
                    .uploadBuilder("/" + file.getOriginalFilename());
            FileMetadata metadata = uploadBuilder.uploadAndFinish(inputStream);

            Attachment attachment = new Attachment();
            attachment.setTask(task);
            attachment.setDropboxFileId(metadata.getId());
            attachment.setFilename(file.getOriginalFilename());
            attachment.setUploadDate(LocalDateTime.now());

            attachmentRepository.save(attachment);

            return new AttachmentDto(attachment.getId(), attachment.getFilename(),
                    attachment.getDropboxFileId(), attachment.getUploadDate());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public List<AttachmentDto> getAttachmentsForTask(Long taskId) {
        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);

        return attachments.stream()
                .map(a -> new AttachmentDto(a.getId(), a.getFilename(),
                        a.getDropboxFileId(), a.getUploadDate()))
                .collect(Collectors.toList());
    }
}
