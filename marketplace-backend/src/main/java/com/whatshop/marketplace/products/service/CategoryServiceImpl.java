package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.products.dto.CategoryDTO;
import com.whatshop.marketplace.products.dto.CreateCategoryRequest;
import com.whatshop.marketplace.products.entity.Category;
import com.whatshop.marketplace.products.repository.CategoryRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> listAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public CategoryDTO create(CreateCategoryRequest request) {
        var name = request.getName();
        var slug = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("La categoría ya existe");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("El slug ya existe");
        }

        var category = Category.builder()
                .name(name)
                .slug(slug)
                .imageUrl(request.getImageUrl())
                .build();

        return toDTO(categoryRepository.save(category));
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .build();
    }
}