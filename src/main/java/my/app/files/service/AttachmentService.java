package my.app.files.service;

import my.app.files.dto.attachment.AttachmentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Long taskId, MultipartFile file);

    List<AttachmentDto> getAttachmentsForTask(Long taskId);
}
