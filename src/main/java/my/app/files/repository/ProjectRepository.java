package my.app.files.repository;

import java.util.List;
import java.util.Optional;
import my.app.files.model.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(Long userId, Pageable pageable);

    Optional<Project> findByIdAndUserEmail(Long id, String email);
}
