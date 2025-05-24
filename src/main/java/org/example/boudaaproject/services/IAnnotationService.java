package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.CoupleTexteDto;
import org.example.boudaaproject.dtos.TaskAnnotatorDTO;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.SubTask;
import org.example.boudaaproject.entities.Task;

import java.util.List;

public interface IAnnotationService {
    List<TaskAnnotatorDTO> getAssignedTasks(Long annotatorId);
    TaskAnnotatorDTO getTaskDetails(Long taskId);
    List<CoupleTexteDto> getCoupleTextesForAnnotator(Long taskId, Long annotatorId);
    List<Category> getTaskCategories(Long taskId);
    void saveAnnotation(Long coupleTexteId, Long annotatorId, Long categoryId, Long taskId);
    Long getSubTaskIdForCoupleTexte(Long coupleTexteId, Long annotatorId, Long taskId);
    void computeAndSetMajorityLabel(CoupleTexte coupleTexte);
}
