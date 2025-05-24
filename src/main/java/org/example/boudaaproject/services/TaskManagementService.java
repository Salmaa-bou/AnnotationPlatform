package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskManagementService implements ITaskManagmentService {

    private final TaskRepository taskRepository;
    private final DatasetRepository datasetRepository;
    private final NlpTaskTypeRepository nlpTaskTypeRepository;
    private final CategoryRepository categoryRepository;
    private final TaskPartitionerService taskPartitionerService;


    @Transactional
    @Override
    public Task createAndAssignTask(TaskDto taskDto) {
        log.info("Creating task with DTO: {}", taskDto);

        if (taskDto.getNombreSousTaches() <= 0) {
            throw new IllegalArgumentException("Le nombre de sous-tâches doit être supérieur à 0.");
        }

        Dataset dataset = datasetRepository.findById(taskDto.getDatasetId())
                .orElseThrow(() -> new IllegalArgumentException("Dataset non trouvé avec id: " + taskDto.getDatasetId()));

        TachesDeNLP tacheNlp = nlpTaskTypeRepository.findById(taskDto.getTacheNlpId())
                .orElseThrow(() -> new IllegalArgumentException("Tâche NLP non trouvée avec id: " + taskDto.getTacheNlpId()));

        List<Category> categories = categoryRepository.findAllById(taskDto.getCategoryIds());
        List<CoupleTexte> couples = new ArrayList<>(dataset.getCoupleTextes());

        if (couples.isEmpty()) {
            throw new IllegalArgumentException("Le dataset ne contient aucun CoupleTexte.");
        }

        if (taskDto.getNombreSousTaches() > couples.size()) {
            throw new IllegalArgumentException("Le nombre de sous-tâches (" + taskDto.getNombreSousTaches() +
                    ") dépasse le nombre de CoupleTexte (" + couples.size() + ").");
        }

        // Créer la tâche (vide de sous-tâches)
        Task task = Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .dataset(dataset)
                .type(tacheNlp)
                .status(Task.TaskStatus.PENDING)
                .textPairs(couples)
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .annotators(new ArrayList<>())
                .assignments(new ArrayList<>())
                .subTasks(new ArrayList<>()) // init vide, remplie après
                .build();

        task = taskRepository.save(task);
        log.info("Task {} persisted.", task.getId());

        // Générer les sous-tâches via le partitionneur
        List<SubTask> generatedSubTasks = taskPartitionerService.partition(task, couples, taskDto.getNombreSousTaches());


        task.getSubTasks().clear();
        for (SubTask subTask : generatedSubTasks) {
            subTask.setTask(task);
            task.getSubTasks().add(subTask);
        }

        log.info("Task {} created with {} sub-tasks.", task.getId(), task.getSubTasks().size());
        return task;
    }

}