package org.example.boudaaproject.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.TaskAnnotatorDTO;
import org.example.boudaaproject.repositories.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Exportserviceee {
    private final TaskRepository taskRepository;
    private final CsvExportService csvExportService;
    private final Logger log = LoggerFactory.getLogger(Exportserviceee.class);

    @Transactional(readOnly = true)

    public List<TaskAnnotatorDTO> getCompletedTasks() {
        log.info("Fetching all completed tasks");
        return taskRepository.findAll().stream()
                .filter(task -> "COMPLETED".equals(task.getStatus().name()))
                .map(this::mapToTaskAnnotatorDTO)
                .collect(Collectors.toList());
    }



    public ResponseEntity<InputStreamResource> exportDataset(Long taskId) {
        log.info("Exporting dataset for completed task ID: {}", taskId);
        return csvExportService.exportTaskToCsv(taskId);
    }


   public TaskAnnotatorDTO mapToTaskAnnotatorDTO(org.example.boudaaproject.entities.Task task) {
        return TaskAnnotatorDTO.builder()
                .id(task.getId())
                .name(task.getName() != null ? task.getName() : "N/A")
                .description(task.getDescription() != null ? task.getDescription() : "N/A")
                .nlpType(task.getType() != null ? task.getType().getName() : "N/A")
                .status(task.getStatus() != null ? task.getStatus().name() : "N/A")
                .createdAt(task.getCreatedAt())
                .build();
    }

}
