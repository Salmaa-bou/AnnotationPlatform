package org.example.boudaaproject.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.SubTask;
import org.example.boudaaproject.entities.Task;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    @NotNull
    private Long datasetId;
    private Long tacheNlpId;
    @NotBlank
    private String name;
    private String description;
    private List<Long> categoryIds;
    private Integer nombreSousTaches;

    private Task.TaskStatus status = Task.TaskStatus.PENDING;
    private List<CoupleTexteDto> couples;
    private List<SubTaskDto> subTasks;
}
