package my.app.files.service;

import my.app.files.dto.attachment.AttachmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Long taskId, MultipartFile file);

    Page<AttachmentDto> getAttachmentsForTask(Long taskId, Pageable pageable);
}
