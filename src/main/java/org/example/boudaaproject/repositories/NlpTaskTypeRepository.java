package org.example.boudaaproject.repositories;

import org.example.boudaaproject.dtos.NlpTachesDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NlpTaskTypeRepository extends JpaRepository<TachesDeNLP,Long> {
    @Query("SELECT new org.example.boudaaproject.dtos.NlpTachesDto(t.id, t.name) FROM TachesDeNLP t")
    List<NlpTachesDto> getAllTachesDto();

}
