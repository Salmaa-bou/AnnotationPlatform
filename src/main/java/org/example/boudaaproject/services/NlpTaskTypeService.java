package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.NlpTachesDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;
import org.example.boudaaproject.repositories.NlpTaskTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NlpTaskTypeService  implements NlpTaskTypeServiceI{
    private final NlpTaskTypeRepository nlpTaskTypeRepository;
    @Override
    public List<TachesDeNLP> getTachesDeNLP(){
        return nlpTaskTypeRepository.findAll();
    }
    @Override
    public List<NlpTachesDto> getAllTachesDto() {
        return nlpTaskTypeRepository.findAll()
                .stream()
                .map(t -> new NlpTachesDto (t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
    @Override
    public TachesDeNLP save(TachesDeNLP tache) {
        tache.getCategories().forEach(cat -> cat.setTache(tache)); // Ensure bi-directional link
        return nlpTaskTypeRepository.save(tache);
    }

}
