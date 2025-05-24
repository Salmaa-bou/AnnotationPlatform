package org.example.boudaaproject.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.SubTaskAssignment;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.repositories.AnnotationRepository;
import org.example.boudaaproject.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final TaskFactoryService taskFactoryService;
    private final AnnotationRepository annotationRepository;

    @Override
    public Optional<Task> getTaskByName(String name) {
        return taskRepository.getTaskByName(name);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        return taskFactoryService.createTask(taskDto);
    }

    @Override
    public Task createFullTask(TaskDto taskDto) {
        return taskFactoryService.createTask(taskDto);
    }

    @Override
    public void deletetask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<Task> getbyname(String name) {
        return taskRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Task> searchParMotcle(String keyword) {
        return taskRepository.searchByKeyword(keyword);
    }

    @Override
    public List<Task> getTaskByDatasetId(Long id) {
        return taskRepository.findByDatasetId(id);
    }

    @Override
    public Task findByIdWithSubTasksAndAssignments(Long id) {
        return taskRepository.findByIdWithSubTasksAndAssignments(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));
    }

    @Override
    @Transactional
    public Task getTaskWithDetails(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));
        task.getSubTasks().size();
        task.getAssignments().size();
        return task;
    }

    @Override
    public List<Task> serashtaskByKeyword(String mot) {
        return taskRepository.searchByKeyword(mot);
    }

    @Override
    public Task gettaskbyid(Long id) {
        return taskRepository.getTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));
    }

    @Override
    public void createTaskwkoulshihitlqinabzafdlmashakilfisolation(TaskDto taskDto) {
        taskFactoryService.createTask(taskDto);
    }
    @Transactional
    @Override
    public void deleteTaskWithDependencies(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Supprimer toutes les annotations liées aux assignments
        List<SubTaskAssignment> assignments = task.getAssignments();
        if (!assignments.isEmpty()) {
            annotationRepository.deleteAllByAssignments(assignments);
        }

        // Puis supprimer la tâche (en cascade : SubTasks, Assignments, etc.)
        taskRepository.delete(task);
    }

}