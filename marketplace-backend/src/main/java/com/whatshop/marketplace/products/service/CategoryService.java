package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.products.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listAll();
    Category getById(UUID id);
    Category create(String name, String imageUrl);
}
