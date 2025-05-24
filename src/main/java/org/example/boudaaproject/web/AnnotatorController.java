package org.example.boudaaproject.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.CoupleTexteDto;
import org.example.boudaaproject.dtos.TaskAnnotatorDTO;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.User;
import org.example.boudaaproject.services.CsvExportService;
import org.example.boudaaproject.services.IAnnotationService;
import org.example.boudaaproject.services.SubTaskAssignmentService;
import org.example.boudaaproject.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/annotator/home")
public class AnnotatorController {
    private final SubTaskAssignmentService subTaskAssignmentService;
    private final UserService userService;
    private final IAnnotationService annotationService;

    private final Logger log = LoggerFactory.getLogger(AnnotatorController.class);

    @GetMapping("/**")
    public String noAccess() {
        log.warn("Unauthorized access attempt");
        return "help";
    }

    @GetMapping("/tasks")
    public String showAssignedTasks(Authentication authentication, Model model) {
        String annotatorIdStr = authentication.getName();
        Long annotatorId = Long.valueOf(annotatorIdStr);
        log.info("Showing tasks for annotator ID: {}", annotatorId);
        List<TaskAnnotatorDTO> tasks = annotationService.getAssignedTasks(annotatorId);
        log.info("Tasks found: {}", tasks.size());
        tasks.forEach(task -> log.info("Task: {}, Type: {}, Categories: {}",
                task.getName(), task.getNlpType(), task.getCategories()));
        model.addAttribute("annotatorId", annotatorId);
        model.addAttribute("tasks", tasks);
        return "annotator/assignedtasks";
    }

    @GetMapping("/visiteprofil")
    public String showVisitedTasks(Authentication authentication, Model model) {
        String annotatorIdStr = authentication.getName();
        Long annotatorId = Long.valueOf(annotatorIdStr);
        log.info("Showing profile for annotator ID: {}", annotatorId);
        User user = userService.findById(annotatorId);
        model.addAttribute("annotatorId", annotatorId);
        model.addAttribute("user", user);
        return "annotator/profil";
    }

    @GetMapping("/tasks/annotate")
    public String annotate(
            @RequestParam("taskId") Long taskId,
            @RequestParam(value = "coupleTexteIndex", defaultValue = "0") int coupleTexteIndex,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        String annotatorIdStr = authentication.getName();
        Long annotatorId = Long.valueOf(annotatorIdStr);
        log.info("Annotating task ID: {} for annotator ID: {}", taskId, annotatorId);

        if (!annotatorIdStr.equals(annotatorId.toString())) {
            log.warn("Annotator ID mismatch: {} vs {}", annotatorId, annotatorIdStr);
            return "redirect:/annotator/home/tasks";
        }

        TaskAnnotatorDTO task = annotationService.getTaskDetails(taskId);
        if (task == null) {
            log.error("Task ID {} not found", taskId);
            redirectAttributes.addFlashAttribute("error", "Task not found.");
            return "redirect:/annotator/home/tasks";
        }

        List<CoupleTexteDto> coupleTextes = annotationService.getCoupleTextesForAnnotator(taskId, annotatorId);
        if (coupleTextes.isEmpty()) {
            log.warn("No CoupleTexte found for task ID {} and annotator ID {}", taskId, annotatorId);
            model.addAttribute("error", "No text pairs available for annotation.");
            return "annotator/annotations";
        }

        coupleTexteIndex = Math.max(0, Math.min(coupleTexteIndex, coupleTextes.size() - 1));
        CoupleTexteDto currentCoupleTexte = coupleTextes.get(coupleTexteIndex);
        Long subTaskId = annotationService.getSubTaskIdForCoupleTexte(currentCoupleTexte.getId(), annotatorId, taskId);
        List<Category> categories = annotationService.getTaskCategories(taskId);

        model.addAttribute("task", task);
        model.addAttribute("coupleTexte", currentCoupleTexte);
        model.addAttribute("categories", categories);
        model.addAttribute("coupleTexteIndex", coupleTexteIndex);
        model.addAttribute("totalCoupleTextes", coupleTextes.size());
        model.addAttribute("annotatorId", annotatorId);
        model.addAttribute("subTaskId", subTaskId);

        return "annotator/annotations";
    }

    @PostMapping("/tasks/annotate")
    public String submitAnnotation(
            @RequestParam("taskId") Long taskId,
            @RequestParam("annotatorId") Long annotatorId,
            @RequestParam("coupleTexteId") Long coupleTexteId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("coupleTexteIndex") int coupleTexteIndex,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String annotatorIdStr = authentication.getName();
        log.info("Submitting annotation for task ID: {}, coupleTexte ID: {}, annotator ID: {}",
                taskId, coupleTexteId, annotatorId);

        if (!annotatorIdStr.equals(annotatorId.toString())) {
            log.warn("Annotator ID mismatch: {} vs {}", annotatorId, annotatorIdStr);
            return "redirect:/annotator/home/tasks";
        }

        try {
            annotationService.saveAnnotation(coupleTexteId, annotatorId, categoryId, taskId);
            int nextIndex = coupleTexteIndex + 1;
            if (nextIndex >= annotationService.getCoupleTextesForAnnotator(taskId, annotatorId).size()) {
                log.info("All text pairs annotated for task ID: {}", taskId);
                redirectAttributes.addFlashAttribute("success", "Task annotation completed!");
                return "redirect:/annotator/home/tasks";
            }
            return String.format("redirect:/annotator/home/tasks/annotate?taskId=%d&annotatorId=%d&coupleTexteIndex=%d",
                    taskId, annotatorId, nextIndex);
        } catch (IllegalStateException e) {
            log.error("Error saving annotation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to save annotation: " + e.getMessage());
            return String.format("redirect:/annotator/home/tasks/annotate?taskId=%d&annotatorId=%d&coupleTexteIndex=%d",
                    taskId, annotatorId, coupleTexteIndex);
        }
    }



    //khassna shi hedra task maghatlea completed SSI statusTask.count(completed) ==subtasks.count(idTask)
    // imta une subtask est completed si il est annote par tout les annotateurs n
    //subtask 1--n annotator   subtask un nmbrcompltexte total dyal assignments liee l une subtask  n*nbrdetextes de cette task ==( coupletextecount(datasetid/taskid)/subtasks.count(taskid)

}