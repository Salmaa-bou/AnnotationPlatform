package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subtasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Task.TaskStatus status = Task.TaskStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "position")
    private int position;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Builder.Default
    @OneToMany(mappedBy = "subTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTaskAssignment> assignments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "subTask", cascade = CascadeType.ALL)
    private List<CoupleTexte> coupleTextes = new ArrayList<>();

    public List<User> getAnnotators() {
        return assignments.stream()
                .map(SubTaskAssignment::getAnnotator)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}