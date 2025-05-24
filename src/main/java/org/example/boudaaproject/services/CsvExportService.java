package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.repositories.CoupleTexteRepository;
import org.example.boudaaproject.repositories.TaskRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final TaskRepository taskRepository;
    private final CoupleTexteRepository coupleTexteRepository;
    private final IAnnotationService annotationService; // For computeAndSetMajorityLabel

    @Transactional
    public ResponseEntity<InputStreamResource> exportTaskToCsv(Long taskId) {
        log.info("Preparing CSV export for task ID: {}", taskId);

        // Fetch all CoupleTexte for the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        List<CoupleTexte> coupleTextes = task.getSubTasks().stream()
                .flatMap(subTask -> subTask.getCoupleTextes().stream())
                .collect(Collectors.toList());

        // Compute and set majority labels for all CoupleTexte
        for (CoupleTexte coupleTexte : coupleTextes) {
            annotationService.computeAndSetMajorityLabel(coupleTexte);
        }

        // Generate CSV content
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.println("index,texte1,texte2,label");
            for (int i = 0; i < coupleTextes.size(); i++) {
                CoupleTexte ct = coupleTextes.get(i);
                String label = ct.getLabel() != null ? ct.getLabel() : "N/A";
                pw.println((i + 1) + "," + ct.getTexte1() + "," + ct.getTexte2() + "," + label);
            }
        }

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=annotations_task_" + taskId + ".csv");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}