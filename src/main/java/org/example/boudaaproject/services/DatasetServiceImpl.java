package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;

import org.example.boudaaproject.dtos.DatasetDtoo;

import org.example.boudaaproject.dtos.DatasetWithTasksDTO;
import org.example.boudaaproject.dtos.TaskDto;
import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.Task;
import org.example.boudaaproject.repositories.CoupleTexteRepository;
import org.example.boudaaproject.repositories.DatasetRepository;
import org.example.boudaaproject.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DatasetServiceImpl implements IDatasetService {

    private final DatasetRepository datasetRepository;
    private final FileStorageService fileStorageService;
  private final ICoupleTexteService coupleTexteService;
    private final CoupleTexteRepository coupleTexteRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @Override
    public List<Dataset> getAllDatasets() {
        return datasetRepository.findAll();
    }

    @Override
    public long countDatasets() {
        return datasetRepository.count();
    }

    @Override
    public Dataset saveDataset(Dataset dataset, MultipartFile file) {
        try {

            String filePath = fileStorageService.storeFile(file);
            dataset.setChemin(filePath);
            dataset.setCreatedAt(LocalDateTime.now());
            Dataset savedDataset = datasetRepository.save(dataset);


            // 3. Lire le CSV et insérer les couples
//            coupleTexteService.parseAndSaveCouples(Path.of(filePath), savedDataset);


            return savedDataset;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du dataset", e);
        }
    }
@Override
    public void deleteDataset(Long datasetid){
        datasetRepository.deleteById(datasetid);
}

@Override
public Optional<Dataset> getDatasetById(Long datasetid) {
        return datasetRepository.findById(datasetid);
}
     @Override
    public List<Dataset>  getDatasetsByByMotcle(String mot) {
         List<Dataset> exist = datasetRepository.findByNameContainingIgnoreCase(mot);
         if (exist.isEmpty()) {
             List<Dataset> datasets = datasetRepository.findByDescriptionIsContainingIgnoreCase(mot);
             if (datasets.isEmpty()) {
                 List<Dataset> datasetss = datasetRepository.findAll();
                 return datasetss;
             }
             return datasets;
         }


         return exist;



}
@Override
    public Dataset updateDataset(Dataset dataset, MultipartFile file) {

        try {
            Dataset existantDataset = datasetRepository.findById(dataset.getId()).orElseThrow(() -> new RuntimeException("Dataset non trouvable"));
            existantDataset.setName(dataset.getName());
            existantDataset.setDescription(dataset.getDescription());
            if (file != null && !file.isEmpty()) {
                String filePath = fileStorageService.storeFile(file);
                existantDataset.setChemin(filePath);

            }
            existantDataset.setCreatedAt(LocalDateTime.now());
             Dataset dataset1 =datasetRepository.save(existantDataset);
                return dataset1;

        }catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du dataset", e);
        }

}
@Override
    public boolean existsByName(String name){
       return  datasetRepository.existsByName(name);
}
@Override
    public List<Dataset> getDatasetByName(String name){
       return  datasetRepository.findByName(name);
}
    @Override
    public List<DatasetDtoo> searchByName(String keyword) {
        return datasetRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(dataset -> new DatasetDtoo(dataset.getId(), dataset.getName()))
                .collect(Collectors.toList());
    }
@Override
    public List<String> getDatasetNames() {
        return datasetRepository.findAllNames();
}
@Override
    public long countDatasetsById(Long datasetId){
        return coupleTexteRepository.countByDatasetId(datasetId);
}

@Override
    public List<TaskDto> getTasksByDatasetId(Long datasetId){
        return taskRepository.findByDataset_Id(datasetId)  ;
}

//    public void parseAndSaveCouples(Path filePath, Dataset dataset) throws IOException {
//        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
//            String line;
//            boolean isFirstLine = true;
//            while ((line = reader.readLine()) != null) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue; // Ignorer l'en-tête
//                }
//
//                String[] parts = line.split(",", -1); // -1 pour inclure les champs vides
//                if (parts.length >= 3) {
//                    CoupleTexte couple = new CoupleTexte();
//                    couple.setTexte1(parts[1].trim().replace("\"", ""));
//                    couple.setTexte2(parts[2].trim().replace("\"", ""));
//                    couple.setDataset(dataset);
//                    coupleTexteRepository.save(couple);
//                }
//            }
//        }
//    }
    @Override

    public Dataset findByIdWithTasks(Long id) {
        return datasetRepository.findWithTasksById(id)
                .orElseThrow(() -> new RuntimeException("Dataset introuvable"));
    }
    @Override
    public DatasetWithTasksDTO getDatasetWithTasks(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found"));
        List<Task> tasks = taskService.getTaskByDatasetId(datasetId);
        return new DatasetWithTasksDTO(dataset, tasks);
    }
}
