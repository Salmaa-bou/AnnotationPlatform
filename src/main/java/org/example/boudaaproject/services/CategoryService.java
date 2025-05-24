package org.example.boudaaproject.services;

import lombok.RequiredArgsConstructor;
import org.example.boudaaproject.dtos.CategoryDto;
import org.example.boudaaproject.dtos.CategoryMapper;
import org.example.boudaaproject.entities.Category;
import org.example.boudaaproject.entities.TachesDeNLP;
import org.example.boudaaproject.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    @Override
    public List<Category>  getCategoryByidNLPtache(TachesDeNLP tachesDeNLP){
        return  categoryRepository.findByTache(tachesDeNLP);
    }
//    @Override
//    public List<Category> getCategoryById(Long tachesId){
//        return categoryRepository.findByTacheId(tachesId);
//    }
    @Override
    public List<CategoryDto> getCategoryDtosByNlpTaskId(Long tachesId) {
        List<Category> categories = categoryRepository.findByTacheId(tachesId);
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }
//@Override
//    // Et cette méthode convertit en DTO
//    public List<CategoryDto> getCategoryDtosByNlpTaskId(Long id) {
//        List<Category> categories = getCategoryById(id);
//        return categories.stream()
//                .map(CategoryMapper::toDto)
//                .collect(Collectors.toList());
//    }
}
