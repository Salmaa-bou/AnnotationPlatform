package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private TachesDeNLP type;

    @ManyToMany
    @JoinTable(
            name = "task_text_pairs",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "text_pair_id")
    )
    @Builder.Default
    private List<CoupleTexte> textPairs = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "task_categories",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>(); // Or ClassesDeNLP

    @ManyToMany
    @JoinTable(
            name = "task_annotators",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> annotators = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    public enum TaskStatus {
        PENDING, COMPLETED
    }

    @Builder.Default
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<SubTask> subTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<SubTaskAssignment> assignments = new ArrayList<>();

    public List<CoupleTexte> getAllCoupleTextes() {
        return subTasks.stream()
                .flatMap(subTask -> subTask.getCoupleTextes().stream())
                .distinct()
                .toList();
    }
}