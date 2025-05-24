package org.example.boudaaproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 4, max = 50)
    private String username;
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
    @NotBlank(message = "L'e-mail est obligatoire")
    @Email(message = "L'e-mail doit être valide")
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String phone;
    private String address;
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z0-9]{10,34}$", message = "RIB invalide")
    private String rib;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState userState=  UserState.ENABLED;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( nullable = false,name = "role_id")
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "annotator")
    private List<SubTaskAssignment> assignments = new ArrayList<>();

}
