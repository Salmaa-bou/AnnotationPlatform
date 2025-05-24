package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter @Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String role ="ANNOTATOR";

}

