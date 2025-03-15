package my.app.files.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.service.AttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file));
    }

    @GetMapping
    public ResponseEntity<List<AttachmentDto>> getAttachmentsForTask(@RequestParam("taskId")
                                                                         Long taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsForTask(taskId));
    }
}
