package org.example.boudaaproject.repositories;

import org.example.boudaaproject.entities.SubTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.boudaaproject.entities.Annotation;

import java.util.List;


@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    List<Annotation> findByTextPairId(Long textPairId);
    boolean existsByTextPairIdAndAnnotatorId(Long textPairId, Long annotatorId);
    boolean existsByTextPairId(Long textPairId);

    @Modifying
    @Query("DELETE FROM Annotation a WHERE a.assignment IN :assignments")
    default void deleteAllByAssignments(@Param("assignments") List<SubTaskAssignment> assignments) {

    }


    @Query("SELECT COUNT(a) FROM Annotation a WHERE a.textPair.id = :textPairId")
    long countByTextPairId(@Param("textPairId") Long textPairId);

}
