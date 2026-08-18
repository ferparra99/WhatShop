package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.products.dto.CategoryDTO;
import com.whatshop.marketplace.products.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> listAll();
    CategoryDTO create(CreateCategoryRequest request);
}