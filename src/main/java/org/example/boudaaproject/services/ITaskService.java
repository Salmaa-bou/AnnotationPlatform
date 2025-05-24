package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Task;

import java.util.List;
import java.util.Optional;

public interface ITaskService{
    Optional<Task> getTaskByName(String Name);
    void deleteTaskWithDependencies(Long taskId);
    List<Task> getAllTasks();
    Task createFullTask(TaskDto taskDto);
     void deletetask(Long id);
    List<Task> searchParMotcle(String keyword);
    List<Task> getTaskByDatasetId(Long id);
    Task findByIdWithSubTasksAndAssignments(Long taskId);
    Task getTaskWithDetails(Long id);
    List<Task> serashtaskByKeyword(String mot);
    List<Task> getbyname(String name);
    Task gettaskbyid(Long id);
    void createTaskwkoulshihitlqinabzafdlmashakilfisolation(TaskDto  taskDto);
    Task createTask(TaskDto taskDto);

//    Optional<Task> getTaskById(int id);
//    void deleteTaskById(int id);
//    void addTask(Task task);
//    void updateTask(Task task);




}
