package org.example.boudaaproject.dtos;



import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskAnnotatorDTO {
    private Long id;
    private String name;
    private String description;
    private String nlpType;
    private List<String> categories;
    private String status;
    private LocalDateTime createdAt;
    private Map<Long, String> subTaskStatuses;
}

