package edu.campus.lostfound.service;

import edu.campus.lostfound.repository.CategoryRepository;
import edu.campus.lostfound.web.dto.CategoryResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;        
@Service
public class CategoryService {
    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAll().stream().map(category -> new CategoryResponse(category.getId(), category.getName())).toList();
    }
}