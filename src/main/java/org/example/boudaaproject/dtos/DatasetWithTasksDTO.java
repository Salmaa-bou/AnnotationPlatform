package org.example.boudaaproject.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.Task;

import java.util.List;
// n affichiw detailsss

@Getter
@Setter
@NoArgsConstructor
public class DatasetWithTasksDTO {
    private Dataset dataset;
    private List<Task> tasks;

    public DatasetWithTasksDTO(Dataset dataset, List<Task> tasks) {
        this.dataset = dataset;
        this.tasks = tasks;

    }
}
