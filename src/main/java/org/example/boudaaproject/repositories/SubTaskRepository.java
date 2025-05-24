package org.example.boudaaproject.repositories;

import org.example.boudaaproject.entities.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask,Long> {
    List<SubTask> findByTaskId(Long taskId);

    @Query("SELECT s FROM SubTask s LEFT JOIN FETCH s.assignments WHERE s.task.id = :taskId")
    List<SubTask> findByTaskIdWithAssignments(@Param("taskId") Long taskId);

}
