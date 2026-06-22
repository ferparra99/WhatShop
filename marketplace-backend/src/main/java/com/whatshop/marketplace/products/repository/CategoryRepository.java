package com.whatshop.marketplace.products.repository;

import com.whatshop.marketplace.products.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
