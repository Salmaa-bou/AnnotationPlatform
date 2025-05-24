package org.example.boudaaproject.repositories;

import jakarta.persistence.LockModeType;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.boudaaproject.entities.Task;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> getTaskByName(String name);
    Optional<Task> getTaskById(Long id);
    List<TaskDto> findByDataset_Id(Long datasetId);
    List<Task> findByNameContainingIgnoreCase(String name);
    List<Task> findByDescriptionIsContainingIgnoreCase(String description);
    List<Task> findByDatasetId(Long datasetId);
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.subTasks st " +
            "LEFT JOIN FETCH st.assignments " +
            "WHERE t.id = :taskId")
    Optional<Task> findByIdWithSubTasksAndAssignments(@Param("taskId") Long taskId);
    // private String name;
    //    private String description;
    //dataset
    @Query("SELECT t FROM Task t WHERE " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.type.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.dataset.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findAndLockById(@Param("id") Long id);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignments WHERE t.id = :taskId")
    Optional<Task> findByIdWithAssignments(@Param("taskId") Long taskId);
    @Query("SELECT t FROM Task t JOIN FETCH t.type LEFT JOIN FETCH t.categories WHERE t.id = :taskId")
    Optional<Task> findByIdWithDetails(Long taskId);



}
