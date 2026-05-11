package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;
import my.app.files.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "Manages comments for tasks.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Add a comment",
            description = "Creates a new comment for a specific task.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@RequestBody @Valid CreateCommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(dto));
    }

    @Operation(summary = "Get comments for a task",
            description = "Retrieves all comments associated with a specific task.")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CommentDto>> getComments(
            @RequestParam Long taskId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId, pageable));
    }
}
