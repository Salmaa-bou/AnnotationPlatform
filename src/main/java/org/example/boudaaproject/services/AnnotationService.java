package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.dtos.CoupleTexteDto;
import org.example.boudaaproject.dtos.TaskAnnotatorDTO;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.repositories.AnnotationRepository;
import org.example.boudaaproject.repositories.CoupleTexteRepository;
import org.example.boudaaproject.repositories.SubTaskAssignmentRepository;
import org.example.boudaaproject.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnotationService implements IAnnotationService {

    private final SubTaskAssignmentRepository subTaskAssignmentRepository;
    private final TaskRepository taskRepository;
    private final AnnotationRepository annotationRepository;
    private final CoupleTexteRepository coupleTexteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskAnnotatorDTO> getAssignedTasks(Long annotatorId) {
        log.info("Fetching tasks for annotator ID: {}", annotatorId);
        List<SubTaskAssignment> assignments = subTaskAssignmentRepository.findByAnnotatorId(annotatorId);
        return assignments.stream()
                .map(SubTaskAssignment::getTask)
                .distinct()
                .map(this::mapToTaskAnnotatorDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskAnnotatorDTO getTaskDetails(Long taskId) {
        log.info("Fetching details for task ID: {}", taskId);
        return taskRepository.findByIdWithDetails(taskId)
                .map(this::mapToTaskAnnotatorDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoupleTexteDto> getCoupleTextesForAnnotator(Long taskId, Long annotatorId) {
        log.info("Fetching CoupleTexte for task ID: {} and annotator ID: {}", taskId, annotatorId);
        List<SubTaskAssignment> assignments = subTaskAssignmentRepository.findByAnnotatorIdAndTaskId(annotatorId, taskId);
        return assignments.stream()
                .map(SubTaskAssignment::getSubTask)
                .flatMap(subTask -> subTask.getCoupleTextes().stream())
                .filter(coupleTexte -> !hasAnnotation(coupleTexte.getId(), annotatorId))
                .map(coupleTexte -> CoupleTexteDto.builder()
                        .id(coupleTexte.getId())
                        .texte1(coupleTexte.getTexte1() != null ? coupleTexte.getTexte1() : "N/A")
                        .texte2(coupleTexte.getTexte2() != null ? coupleTexte.getTexte2() : "N/A")
                        .build())
                .distinct()
                .sorted(Comparator.comparing(CoupleTexteDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getTaskCategories(Long taskId) {
        log.info("Fetching categories for task ID: {}", taskId);
        return taskRepository.findById(taskId)
                .map(Task::getCategories)
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void saveAnnotation(Long coupleTexteId, Long annotatorId, Long categoryId, Long taskId) {
        log.info("Saving annotation for coupleTexte ID: {}, annotator ID: {}, category ID: {}",
                coupleTexteId, annotatorId, categoryId);
        SubTaskAssignment assignment = subTaskAssignmentRepository
                .findByAnnotatorIdAndTaskId(annotatorId, taskId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No assignment found for annotator and task"));

        CoupleTexte coupleTexte = coupleTexteRepository.findById(coupleTexteId);


        Category category = taskRepository.findById(taskId)
                .map(Task::getCategories)
                .orElseThrow(() -> new IllegalStateException("Task not found"))
                .stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        Annotation annotation = Annotation.builder()
                .textPair(coupleTexte)
                .annotator(assignment.getAnnotator())
                .classChosen(category)
                .assignment(assignment)
                .createdAt(LocalDateTime.now())
                .build();

        annotationRepository.save(annotation);

        updateSubTaskAndTaskStatus(assignment.getSubTask(), taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSubTaskIdForCoupleTexte(Long coupleTexteId, Long annotatorId, Long taskId) {
        log.info("Fetching SubTask ID for coupleTexte ID: {}, annotator ID: {}, task ID: {}",
                coupleTexteId, annotatorId, taskId);
        return subTaskAssignmentRepository
                .findByAnnotatorIdAndTaskId(annotatorId, taskId)
                .stream()
                .map(SubTaskAssignment::getSubTask)
                .filter(subTask -> subTask.getCoupleTextes().stream()
                        .anyMatch(ct -> ct.getId().equals(coupleTexteId)))
                .map(SubTask::getId)
                .findFirst()
                .orElse(null);
    }

    private void updateSubTaskAndTaskStatus(SubTask subTask, Long taskId) {
        Long subTaskId = subTask.getId();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task not found"));

        // Get all annotators assigned to this subtask
        List<SubTaskAssignment> assignments = subTaskAssignmentRepository.findBySubTaskId(subTaskId);
        int totalAnnotators = assignments.size();
        if (totalAnnotators == 0) return; // No annotators assigned

        // Check if all annotators have annotated each CoupleTexte
        List<CoupleTexte> coupleTextes = subTask.getCoupleTextes();
        boolean subTaskCompleted = coupleTextes.stream()
                .allMatch(coupleTexte -> {
                    long annotatedByCount = annotationRepository.countByTextPairId(coupleTexte.getId());
                    return annotatedByCount >= totalAnnotators; // All annotators must annotate
                });

        if (subTaskCompleted && subTask.getStatus() != Task.TaskStatus.COMPLETED) {
            subTask.setStatus(Task.TaskStatus.COMPLETED);
            log.info("SubTask ID: {} marked as COMPLETED", subTaskId);

            // Compute majority label only when all annotations are present
            for (CoupleTexte coupleTexte : coupleTextes) {
                computeAndSetMajorityLabel(coupleTexte);
            }
        }

        // Check if all subtasks are completed for the task
        boolean taskCompleted = task.getSubTasks().stream()
                .allMatch(st -> st.getStatus() == Task.TaskStatus.COMPLETED);
        if (taskCompleted && task.getStatus() != Task.TaskStatus.COMPLETED) {
            task.setStatus(Task.TaskStatus.COMPLETED);
            log.info("Task ID: {} marked as COMPLETED", taskId);
        }
    }
@Override
    @Transactional
    public void computeAndSetMajorityLabel(CoupleTexte coupleTexte) {
        log.info("Computing majority label for CoupleTexte ID: {}", coupleTexte.getId());
        List<Annotation> annotations = annotationRepository.findByTextPairId(coupleTexte.getId());
        if (annotations.isEmpty()) {
            log.warn("No annotations found for CoupleTexte ID: {}", coupleTexte.getId());
            return;
        }

        // Ensure all annotators have annotated before setting the majority label
        Long subTaskId = coupleTexte.getSubTask().getId();
        long totalAnnotators = subTaskAssignmentRepository.countBySubTaskId(subTaskId);
        if (annotations.size() < totalAnnotators) {
            log.info("Not all annotators have annotated CoupleTexte ID: {}. Skipping majority label.", coupleTexte.getId());
            return;
        }

        Map<Category, Long> voteCounts = annotations.stream()
                .collect(Collectors.groupingBy(
                        Annotation::getClassChosen,
                        Collectors.counting()
                ));

        Category majorityCategory = voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (majorityCategory != null) {
            coupleTexte.setLabel(majorityCategory.getName());
            coupleTexteRepository.save(coupleTexte);
            log.info("Majority label set to {} for CoupleTexte ID: {}",
                    majorityCategory.getName(), coupleTexte.getId());
        } else {
            log.warn("No majority label determined for CoupleTexte ID: {} (possible tie)", coupleTexte.getId());
        }
    }

    private boolean hasAnnotation(Long coupleTexteId, Long annotatorId) {
        return annotationRepository.existsByTextPairIdAndAnnotatorId(coupleTexteId, annotatorId);
    }

    private TaskAnnotatorDTO mapToTaskAnnotatorDTO(Task task) {
        Map<Long, String> subTaskStatuses = task.getSubTasks().stream()
                .collect(Collectors.toMap(
                        SubTask::getId,
                        st -> st.getStatus() != null ? st.getStatus().name() : "N/A"
                ));
        return TaskAnnotatorDTO.builder()
                .id(task.getId())
                .name(task.getName() != null ? task.getName() : "N/A")
                .description(task.getDescription() != null ? task.getDescription() : "N/A")
                .nlpType(task.getType() != null ? task.getType().getName() : "N/A")
                .categories(task.getCategories() != null ?
                        task.getCategories().stream()
                                .map(category -> category.getName() != null ? category.getName() : "N/A")
                                .collect(Collectors.toList()) :
                        List.of("N/A"))
                .status(task.getStatus() != null ? task.getStatus().name() : "N/A")
                .createdAt(task.getCreatedAt())
                .subTaskStatuses(subTaskStatuses)
                .build();
    }

    // Helper method to count annotators for a subtask
    private long countAnnotatorsForSubTask(Long subTaskId) {
        return subTaskAssignmentRepository.countBySubTaskId(subTaskId);
    }
}