package my.app.files.repository;

import java.util.Optional;
import my.app.files.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);
}
