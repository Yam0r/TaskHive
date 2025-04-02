package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.service.AttachmentService;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file));
    }

    @Operation(summary = "Get all attachments for a task",
            description = "Retrieves all attachments associated with a specific task")
    @GetMapping
    public ResponseEntity<List<AttachmentDto>> getAttachmentsForTask(@RequestParam("taskId")
                                                                         Long taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsForTask(taskId));
    }
}
