package org.example.boudaaproject.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoupleTexteDto {
    private Long id;

    @NotBlank(message = "Texte 1 est requis")
    private String texte1;

    @NotBlank(message = "Texte 2 est requis")
    private String texte2;

    private String label;

    private Long datasetId;

}
