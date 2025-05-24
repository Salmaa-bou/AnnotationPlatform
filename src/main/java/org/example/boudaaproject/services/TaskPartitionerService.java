package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskPartitionerService {

    private static final int MIN_ANNOTATORS_PER_SUBTASK = 3;

    private final SubTaskRepository subTaskRepository;
    private final UserRepository userRepository;
    private final SubTaskAssignmentRepository assignmentRepository;
    private final CoupleTexteRepository coupleTexteRepository;

    private Set<User> selectAnnotators(List<User> annotators) {
        Set<User> selected = new HashSet<>();
        List<User> shuffledAnnotators = new ArrayList<>(annotators);
        Collections.shuffle(shuffledAnnotators);
        for (User user : shuffledAnnotators) {
            if (selected.size() >= MIN_ANNOTATORS_PER_SUBTASK) break;
            selected.add(user);
        }
        return selected;
    }

    @Transactional
    public List<SubTask> partition(Task task, List<CoupleTexte> couples, int nombreSousTaches) {
        if (nombreSousTaches <= 0 || nombreSousTaches > couples.size()) {
            throw new IllegalArgumentException("Invalid sub-task count: " + nombreSousTaches);
        }
        if (task.getId() == null) {
            throw new IllegalStateException("Task must be persisted.");
        }

        List<User> annotators = userRepository.findByRoleName("ANNOTATOR").stream()
                .filter(user -> user.getUserState() == UserState.ENABLED)
                .toList();
        if (annotators.size() < MIN_ANNOTATORS_PER_SUBTASK) {
            throw new IllegalStateException("Need at least " + MIN_ANNOTATORS_PER_SUBTASK + " active annotators.");
        }

        int baseSize = couples.size() / nombreSousTaches;
        int remainder = couples.size() % nombreSousTaches;
        List<SubTask> subTasks = new ArrayList<>();
        List<SubTaskAssignment> assignments = new ArrayList<>();
        int startIndex = 0;

        for (int i = 0; i < nombreSousTaches; i++) {
            int size = baseSize + (i < remainder ? 1 : 0);
            int endIndex = startIndex + size;
            List<CoupleTexte> partition = couples.subList(startIndex, endIndex);
            if (partition.isEmpty()) {
                log.warn("Empty partition for sub-task {}.", i + 1);
                continue;
            }

            SubTask subTask = SubTask.builder()
                    .name("SubTask " + (i + 1))
                    .position(i + 1)
                    .status(Task.TaskStatus.PENDING)
                    .task(task)
                    .createdAt(LocalDateTime.now())
                    .build();
            subTask = subTaskRepository.save(subTask);
            subTasks.add(subTask);
            log.info("Created SubTask {} for Task {}.", subTask.getId(), task.getId());

            for (CoupleTexte couple : partition) {
                couple.setSubTask(subTask);
                subTask.getCoupleTextes().add(couple);
            }
            coupleTexteRepository.saveAll(partition);
            log.info("Assigned {} CoupleTexte to SubTask {}.", partition.size(), subTask.getId());

            Set<User> selectedAnnotators = selectAnnotators(annotators);
            if (selectedAnnotators.size() < MIN_ANNOTATORS_PER_SUBTASK) {
                throw new IllegalStateException("Not enough annotators for sub-task " + subTask.getId());
            }

            for (User annotator : selectedAnnotators) {
                if (!assignmentRepository.existsByAnnotatorIdAndSubTaskId(annotator.getId(), subTask.getId())) {
                    SubTaskAssignment assignment = SubTaskAssignment.builder()
                            .task(task)
                            .subTask(subTask)
                            .annotator(annotator)
                            .assignedAt(LocalDateTime.now())
                            .build();
                    assignments.add(assignment);
                    log.info("Assigned annotator {} to sub-task {}.", annotator.getId(), subTask.getId());
                }
            }

            startIndex = endIndex;
        }


        task.getAnnotators().clear();
        task.getAnnotators().addAll(assignments.stream()
                .map(SubTaskAssignment::getAnnotator)
                .distinct()
                .toList());
        assignmentRepository.saveAll(assignments);
        log.info("Task {}: Created {} sub-tasks with {} assignments.", task.getId(), subTasks.size(), assignments.size());
        return subTasks;
    }
}