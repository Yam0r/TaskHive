package my.app.files.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labels", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new HashSet<>();
}
