package my.app.files.repository;

import my.app.files.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Task> findAllTasksByAssigneeId(Long assigneeId);
}
