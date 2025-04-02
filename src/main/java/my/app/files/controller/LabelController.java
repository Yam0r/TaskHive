package my.app.files.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.service.LabelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Label", description = "Manages labels for tasks")
@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @Operation(summary = "Create a label", description = "Creates a new label.")
    @PostMapping
    public ResponseEntity<LabelDto> createLabel(@Valid @RequestBody CreateLabelRequestDto dto) {
        return ResponseEntity.ok(labelService.createLabel(dto));
    }

    @Operation(summary = "Get all labels", description = "Retrieves a list of all labels.")
    @GetMapping
    public ResponseEntity<List<LabelDto>> getAllLabels() {
        return ResponseEntity.ok(labelService.getAllLabels());
    }

    @Operation(summary = "Update a label", description = "Updates an existing label by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> updateLabel(@PathVariable Long id,
                                                @Valid @RequestBody UpdateLabelRequestDto dto) {
        return ResponseEntity.ok(labelService.updateLabel(id, dto));
    }

    @Operation(summary = "Delete a label", description = "Deletes a label by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}
