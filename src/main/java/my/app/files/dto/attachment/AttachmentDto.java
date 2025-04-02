package my.app.files.dto.attachment;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttachmentDto {
    private Long id;
    private String filename;
    private String dropboxFileId;
    private LocalDateTime uploadDate;
}
