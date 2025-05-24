package org.example.boudaaproject.services;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.UserState;
import org.springframework.web.multipart.MultipartFile;
import org.example.boudaaproject.entities.User;
import java.util.List;
import java.util.Optional;

public interface AdminService {

  void createAnnotator(User user);
  void deleteUser(Long id);
  List<User> listAnnotators();
  long countAnnotators();
  List<User> getAllAnnotators();
//  void deleteAnnotator(Long id);
  Optional<User> getAnnotator(Long id);
  void promoteToAdmin(Long id);
  void updateAnnotator(User user);
  List<User> chercherAnnotator(String username);
  void toggleUserStatus(Long id);
  void updateUserState(Long id, UserState newState);
//  Dataset createDataset(
//          DatasetDto dto, MultipartFile csvFile);
//  void deleteDataset(Long id);
//  List<Dataset> getAllDatasets();
//
//  Task createTaskForDataset(Long datasetId, TaskDto dto);
//  void assignAnnotatorsToTask(Long taskId, List<Long> annotatorIds);
//  void deleteTask(Long taskId);
//  List<Task> getTasksByDataset(Long datasetId);
  //  void importDataset(MultipartFile file);
//  void assignTasksAutomatically(Long datasetId);
//  void defineCategories(List<String> categoryNames);

// void importDataset(String name, List<String[]> textPairs);
//  List<Dataset> getAllDatasets();
//
////  void createTask(String title, Long datasetId, List<Long> coupleIds);
////  List<Task> getAllTasks();
//long countDatasets();
//  long countTasks();
//  long countAnnotations();

}
