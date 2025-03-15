package my.app.files.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attachments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false, unique = true)
    private String dropboxFileId;  // Уникальный идентификатор файла в Dropbox

    @Column(nullable = false)
    private String filename;  // Оригинальное название файла

    @Column(nullable = false)
    private LocalDateTime uploadDate;
}
