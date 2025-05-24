package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"text_pair_id", "annotator_id"}))
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "text_pair_id", nullable = false)
    private CoupleTexte textPair;

    @ManyToOne
    @JoinColumn(name = "annotator_id", nullable = false)
    private User annotator;
@ManyToOne
@JoinColumn(name = "category_id", nullable = false)
    private Category classChosen;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private SubTaskAssignment assignment;





}
