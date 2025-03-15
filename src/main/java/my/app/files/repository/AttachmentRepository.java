package my.app.files.repository;

import my.app.files.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long>{
    List<Attachment> findByTaskId(Long taskId);
}

