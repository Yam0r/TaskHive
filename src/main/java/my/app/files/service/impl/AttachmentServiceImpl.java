package my.app.files.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DbxClientV2 dropboxClient;
    private final AttachmentMapper attachmentMapper;

    @Override
    @Transactional
    public AttachmentDto uploadAttachment(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        FileMetadata metadata;
        try (InputStream inputStream = file.getInputStream()) {
            metadata = dropboxClient.files()
                    .uploadBuilder("/" + file.getOriginalFilename())
                    .uploadAndFinish(inputStream);
        } catch (IOException | DbxException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFilename(file.getOriginalFilename());
        attachment.setDropboxFileId(metadata.getId());
        attachment.setUploadDate(LocalDateTime.now());
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public Page<AttachmentDto> getAttachmentsForTask(Long taskId, Pageable pageable) {
        return attachmentRepository.findByTaskId(taskId, pageable)
                .map(attachmentMapper::toDto);
    }
}
