package org.example.boudaaproject.repositories;

import org.example.boudaaproject.entities.SubTaskAssignment;
import org.example.boudaaproject.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTaskAssignmentRepository  extends JpaRepository<SubTaskAssignment, Long> {
    boolean existsByAnnotatorIdAndSubTaskId(Long annotatorId, Long subTaskId);
    @Query("SELECT a FROM SubTaskAssignment a JOIN FETCH a.task WHERE a.annotator.id = :annotatorId")
    List<SubTaskAssignment>  findByAnnotatorId(Long annotatorId);

    List<SubTaskAssignment> findByAnnotatorIdAndTaskId(Long annotatorId, Long taskId);
    @Query("SELECT COUNT(s) FROM SubTaskAssignment s WHERE s.subTask.id = :subTaskId")
    long countBySubTaskId(@Param("subTaskId") Long subTaskId);
    List<SubTaskAssignment> findBySubTaskId(Long subTaskId);






}
