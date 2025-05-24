package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
///  made to seperate creation and assignment phases
///     /// // i d tried so hard to make the proceesssss modular isoler la creation  repartition et  l affectation ms j les erreurs de hibernate j essaye le fetch ms hibernate apres la creation il ne peut pas surcharger deux listes qui ont relations lazy donc je tombe dans lazyinitialzationexception
///     /// si j isole le logique les valeurs qui sont nulles par defaut rien s insert apres pour assignments table
///     /// donc single service to faire l affaire

@Slf4j
@Service
@RequiredArgsConstructor
public class SubTaskAssignmentService {
    private static final int MIN_ANNOTATORS_PER_SUBTASK = 3;

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final SubTaskAssignmentRepository assignmentRepository;

    private Set<User> selectAnnotators(List<User> annotators, Set<User> alreadyAssigned) {
        Set<User> selected = new HashSet<>();
        List<User> shuffledAnnotators = new ArrayList<>(annotators);
        Collections.shuffle(shuffledAnnotators, new Random());
        int index = 0;
        int attempts = 0;
        int maxAttempts = annotators.size() * 2;

        while (selected.size() < MIN_ANNOTATORS_PER_SUBTASK && attempts < maxAttempts) {
            User candidate = shuffledAnnotators.get(index % annotators.size());
            index++;
            attempts++;
            if (!alreadyAssigned.contains(candidate)) {
                selected.add(candidate);
            }
        }
        return selected;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void assignAnnotatorsToSubTasks(Long taskId) {
        Task task = taskRepository.findByIdWithSubTasksAndAssignments(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tâche introuvable avec l'id: " + taskId));

        List<SubTask> subTasks = subTaskRepository.findByTaskIdWithAssignments(taskId);
        if (subTasks.isEmpty()) {
            throw new IllegalStateException("Aucune sous-tâche trouvée pour la tâche " + taskId);
        }

        List<User> annotators = userRepository.findByRoleName("ANNOTATOR").stream()
                .filter(user -> user.getUserState() == UserState.ENABLED)
                .toList();
        if (annotators.size() < MIN_ANNOTATORS_PER_SUBTASK) {
            throw new IllegalStateException("Il faut au moins " + MIN_ANNOTATORS_PER_SUBTASK + " annotateurs actifs.");
        }

        List<SubTaskAssignment> newAssignments = new ArrayList<>();

        for (SubTask subTask : subTasks) {
            if (subTask.getCoupleTextes().isEmpty()) {
                log.warn("Sous-tâche {} ignorée : aucun CoupleTexte.", subTask.getId());
                continue;
            }

            Set<User> alreadyAssigned = subTask.getAssignments().stream()
                    .map(SubTaskAssignment::getAnnotator)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<User> selectedAnnotators = selectAnnotators(annotators, alreadyAssigned);
            if (selectedAnnotators.size() < MIN_ANNOTATORS_PER_SUBTASK) {
                throw new IllegalStateException("Impossible d’assigner " + MIN_ANNOTATORS_PER_SUBTASK +
                        " annotateurs uniques à la sous-tâche " + subTask.getId());
            }

            for (User annotator : selectedAnnotators) {
                if (!assignmentRepository.existsByAnnotatorIdAndSubTaskId(annotator.getId(), subTask.getId())) {
                    SubTaskAssignment assignment = SubTaskAssignment.builder()
                            .task(task)
                            .subTask(subTask)
                            .annotator(annotator)
                            .assignedAt(LocalDateTime.now())
                            .build();
                    subTask.getAssignments().add(assignment);
                    task.getAssignments().add(assignment);
                    newAssignments.add(assignment);
                    log.info("Annotator '{}' assigned to sub-task {}.", annotator.getUsername(), subTask.getId());
                }
            }
        }

        assignmentRepository.saveAll(newAssignments);
        // Sync task's annotators
        task.getAnnotators().clear();
        task.getAnnotators().addAll(newAssignments.stream()
                .map(SubTaskAssignment::getAnnotator)
                .distinct()
                .toList());
        taskRepository.save(task);
        log.info("Task {}: {} assignments saved.", taskId, newAssignments.size());
    }
}