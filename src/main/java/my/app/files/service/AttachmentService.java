package my.app.files.service;

import java.util.List;
import my.app.files.dto.attachment.AttachmentDto;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Long taskId, MultipartFile file);

    List<AttachmentDto> getAttachmentsForTask(Long taskId);
}
