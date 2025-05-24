package org.example.boudaaproject.repositories;

import org.example.boudaaproject.dtos.CategoryDto;
import org.example.boudaaproject.dtos.DatasetDtoo;

import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.Dataset;
import org.example.boudaaproject.entities.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Integer> {
    void deleteById(Long id);
    List<Dataset> findByName(String  name);
    List<Dataset> findByNameContainingIgnoreCase(String name);
    List<Dataset> findByDescriptionIsContainingIgnoreCase(String description);

    Optional<Dataset> findById(Long id);
    @EntityGraph(attributePaths = "taches")
    Optional<Dataset> findWithTasksById(Long id);

    boolean existsByName(String name);
    @Query("SELECT d.name FROM Dataset d")
    List<String> findAllNames();







}
