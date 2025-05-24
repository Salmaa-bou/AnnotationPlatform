package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "textes_pair")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class CoupleTexte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texte1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texte2;

    private String label; // Final computed label

    @ManyToOne
    @JoinColumn(name = "dataset_id", nullable = true)
    private Dataset dataset;

    @ManyToOne
    @JoinColumn(name = "sub_task_id")
    private SubTask subTask;

    @Builder.Default
    @OneToMany(mappedBy = "textPair", cascade = CascadeType.ALL)
    private List<Annotation> annotations = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "textPairs")
    private List<Task> tasks = new ArrayList<>();
}