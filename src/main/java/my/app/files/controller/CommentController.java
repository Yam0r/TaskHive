package my.app.files.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.comment.CommentDto;
import my.app.files.dto.comment.CreateCommentRequestDto;
import my.app.files.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@RequestBody @Valid CreateCommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId));
    }
}

