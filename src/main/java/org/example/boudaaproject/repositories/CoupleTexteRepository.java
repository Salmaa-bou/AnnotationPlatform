package org.example.boudaaproject.repositories;

import org.example.boudaaproject.entities.CoupleTexte;
import org.example.boudaaproject.entities.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

@Repository
public interface CoupleTexteRepository extends JpaRepository<CoupleTexte, Integer> {
    long countByDatasetId(Long id);
    CoupleTexte findById(Long id);
}
