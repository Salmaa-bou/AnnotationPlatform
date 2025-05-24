package org.example.boudaaproject.repositories;

import org.example.boudaaproject.dtos.CategoryDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
boolean existsByName(String name);

    List<Category> findByTache(TachesDeNLP tache);

    List<Category> findByTacheId(Long tacheId);
    List<Category> findAllById(Iterable<Long> ids);

}
