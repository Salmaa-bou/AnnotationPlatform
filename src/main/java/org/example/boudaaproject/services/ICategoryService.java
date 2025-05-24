package org.example.boudaaproject.services;

import org.example.boudaaproject.dtos.CategoryDto;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
     List<Category> getCategoryByidNLPtache(TachesDeNLP tachesDeNLP);
//     List<CategoryDto> getCategoryById(Long tachesId);
    List<CategoryDto> getCategoryDtosByNlpTaskId(Long id);

}
