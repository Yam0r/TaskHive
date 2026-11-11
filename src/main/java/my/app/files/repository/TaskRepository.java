package my.app.files.repository;

import java.util.List;
import java.util.Optional;
import my.app.files.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Task> findAllTasksByAssigneeId(Long assigneeId);

    Optional<Task> findByIdAndProjectUserEmail(Long id, String email);
}
