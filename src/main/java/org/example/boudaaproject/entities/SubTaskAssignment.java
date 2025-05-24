package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subtask_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"annotator_id", "sub_task_id"})
        },
        indexes = {
                @Index(columnList = "annotator_id"),
                @Index(columnList = "sub_task_id"),
                @Index(columnList = "task_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sub_task_id")
    private SubTask subTask;

    @ManyToOne
    @JoinColumn(name = "annotator_id", nullable = false)
    private User annotator;

    private LocalDateTime assignedAt = LocalDateTime.now();
}