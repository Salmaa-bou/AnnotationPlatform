package org.example.boudaaproject.dtos;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DtoTask {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String datasetName;
    private String tacheNlpName;
    private List<String> categoryNames;
    private int nombreSousTaches;
}
