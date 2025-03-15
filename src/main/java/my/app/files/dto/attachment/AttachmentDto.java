package my.app.files.dto.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AttachmentDto {
    private Long id;
    private String filename;
    private String dropboxFileId;
    private LocalDateTime uploadDate;
}
