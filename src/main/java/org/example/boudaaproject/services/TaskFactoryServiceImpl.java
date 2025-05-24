package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.*;
import org.example.boudaaproject.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskFactoryServiceImpl implements TaskFactoryService {
    private final TaskRepository taskRepository;
    private final DatasetRepository datasetRepository;
    private final NlpTaskTypeRepository nlpTaskTypeRepository;
    private final CategoryRepository categoryRepository; // Or ClassesDeNLPRepository
    private final TaskPartitionerService taskPartitionerService;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Task createTask(TaskDto taskDto) {
        log.info("Creating task with DTO: {}", taskDto);
        if (taskDto.getNombreSousTaches() <= 0) {
            throw new IllegalArgumentException("Le nombre de sous-tâches doit être supérieur à 0.");
        }

        Dataset dataset = datasetRepository.findById(taskDto.getDatasetId())
                .orElseThrow(() -> new IllegalArgumentException("Dataset non trouvé avec id: " + taskDto.getDatasetId()));
        TachesDeNLP tacheNlp = nlpTaskTypeRepository.findById(taskDto.getTacheNlpId())
                .orElseThrow(() -> new IllegalArgumentException("Tâche NLP non trouvée avec id: " + taskDto.getTacheNlpId()));
        List<Category> categories = categoryRepository.findAllById(taskDto.getCategoryIds()); // Or ClassesDeNLP

        List<CoupleTexte> couples = new ArrayList<>(dataset.getCoupleTextes());
        if (couples.isEmpty()) {
            throw new IllegalArgumentException("Le dataset ne contient aucun CoupleTexte.");
        }
        if (taskDto.getNombreSousTaches() > couples.size()) {
            throw new IllegalArgumentException("Le nombre de sous-tâches (" + taskDto.getNombreSousTaches() +
                    ") dépasse le nombre de CoupleTexte (" + couples.size() + ").");
        }

        Task task = Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .dataset(dataset)
                .type(tacheNlp)
                .status(Task.TaskStatus.PENDING)
                .textPairs(couples)
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .annotators(new ArrayList<>()) // Initialize annotators
                .assignments(new ArrayList<>())
                .subTasks(new ArrayList<>())
                .build();

        task = taskRepository.save(task);
        log.info("Task {} persisted.", task.getId());

        List<SubTask> subTasks = taskPartitionerService.partition(task, couples, taskDto.getNombreSousTaches());
        task.setSubTasks(subTasks);

        log.info("Task {} created with {} sub-tasks.", task.getId(), subTasks.size());
        return task;
    }

    @Override
    public TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .name(task.getName())
                .description(task.getDescription())
                .datasetId(task.getDataset().getId())
                .tacheNlpId(task.getType().getId())
                .status(task.getStatus())
                .categoryIds(task.getCategories().stream().map(Category::getId).toList())
                .nombreSousTaches(task.getSubTasks() != null ? task.getSubTasks().size() : 0)
                .build();
    }
}