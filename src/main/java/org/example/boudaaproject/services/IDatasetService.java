package org.example.boudaaproject.services;


import org.example.boudaaproject.dtos.DatasetDtoo;
import org.example.boudaaproject.dtos.DatasetWithTasksDTO;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.Dataset;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IDatasetService {
    Dataset saveDataset(Dataset dataset, MultipartFile file);
    List<Dataset> getAllDatasets();
    void deleteDataset(Long datasetId);
    Optional<Dataset> getDatasetById(Long datasetId);
    List<Dataset> getDatasetsByByMotcle(String motcle);
    Dataset updateDataset(Dataset dataset, MultipartFile file);
    boolean existsByName(String name);
    List<Dataset> getDatasetByName(String name);
  List<String> getDatasetNames();
//    void parseAndSaveCouples(Path filePath, Dataset dataset) throws IOException;
//    Dataset getDatasetById(Long id);
//    void assignAnnotators(Long datasetId, List<Long> annotatorIds);
//    void removeAnnotator(Long datasetId, Long annotatorId);
//    double getDatasetProgress(Long datasetId);
//    void exportDataset(Long datasetId, String format);
    long countDatasets();
    long countDatasetsById(Long datasetId);
    List<DatasetDtoo>  searchByName(String keyword);
    List<TaskDto> getTasksByDatasetId(Long datasetId);
    Dataset findByIdWithTasks(Long id);
    DatasetWithTasksDTO getDatasetWithTasks(Long id);

}
