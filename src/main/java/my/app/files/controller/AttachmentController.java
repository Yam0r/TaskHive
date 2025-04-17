package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachment", description = "Manages file attachments for tasks and projects.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @Operation(summary = "Upload an attachment",
            description = "Uploads an attachment for a specific task")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file));
    }

    @Operation(summary = "Get all attachments for a task",
            description = "Retrieves all attachments associated with a specific task")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AttachmentDto>> getAttachmentsForTask(
            @RequestParam("taskId") Long taskId,
            Pageable pageable) {
        return ResponseEntity.ok(attachmentService.getAttachmentsForTask(taskId, pageable));
    }
}
