package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "classesdeNLP",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tache_nlp_id", "name"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=false)
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tache_nlp_id")
    private TachesDeNLP tache; // correspond à la tâche NLP globale (e.g., classification, NLI, etc.)
}
//    @ManyToOne
//    private Task task;

