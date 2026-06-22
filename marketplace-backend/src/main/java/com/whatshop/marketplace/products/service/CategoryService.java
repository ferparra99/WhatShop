package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.products.entity.Category;
import com.whatshop.marketplace.products.repository.CategoryRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    @Transactional
    public Category create(String name, String imageUrl) {
        var slug = name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");

        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("La categoría ya existe");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("El slug ya existe");
        }

        var category = Category.builder()
                .name(name)
                .slug(slug)
                .imageUrl(imageUrl)
                .build();

        return categoryRepository.save(category);
    }
}
