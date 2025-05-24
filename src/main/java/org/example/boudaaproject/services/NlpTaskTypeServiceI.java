package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.NlpTachesDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;

import java.util.List;

public interface NlpTaskTypeServiceI {
    List<TachesDeNLP> getTachesDeNLP();
    List<NlpTachesDto> getAllTachesDto();
    TachesDeNLP save(TachesDeNLP tache);
}
