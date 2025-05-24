package org.example.boudaaproject.dtos;

import jakarta.persistence.Column;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Dataset;
import org.springframework.stereotype.Component;

@Component
public class CoupleTexteMapper {

    public CoupleTexteDto toDto(CoupleTexte entity) {
        return CoupleTexteDto.builder()
                .id(entity.getId())
                .texte1(entity.getTexte1())
                .texte2(entity.getTexte2())
                .label(entity.getLabel())
                .datasetId(entity.getDataset() != null ? entity.getDataset().getId() : null)
                .build();
    }

    public CoupleTexte toEntity(CoupleTexteDto dto, Dataset dataset) {
        return CoupleTexte.builder()
                .id(dto.getId())
                .texte1(dto.getTexte1())
                .texte2(dto.getTexte2())
                .label(dto.getLabel())
                .dataset(dataset)
                .build();
    }
}
