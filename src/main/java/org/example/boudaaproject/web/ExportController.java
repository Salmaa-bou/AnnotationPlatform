package org.example.boudaaproject.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.TaskAnnotatorDTO;

import org.example.boudaaproject.services.Exportserviceee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/gestiondesexports")
public class ExportController {
    private final Exportserviceee exportserviceee;
    private final Logger log = LoggerFactory.getLogger(ExportController.class);

    @GetMapping("/export")
    public String exportss(Model model) {
        log.info("Admin accessing export management page");
        List<TaskAnnotatorDTO> completedTasks = exportserviceee.getCompletedTasks();
        log.info("Found {} completed tasks for export", completedTasks.size());
        model.addAttribute("tasks", completedTasks);
        return "/admin/gestiondesexports/export";
    }

    @GetMapping("/export/{taskId}")
    public ResponseEntity<InputStreamResource> exportTaskDataset(
            @PathVariable("taskId") Long taskId,
            HttpServletResponse response) {
        log.info("Admin exporting dataset for task ID: {}", taskId);
        return exportserviceee.exportDataset(taskId);
    }
}