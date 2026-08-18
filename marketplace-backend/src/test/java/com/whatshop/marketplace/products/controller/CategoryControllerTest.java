package com.whatshop.marketplace.products.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.products.dto.CategoryDTO;
import com.whatshop.marketplace.products.dto.CreateCategoryRequest;
import com.whatshop.marketplace.products.service.CategoryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController")
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryService);
    }

    @Test
    @DisplayName("GET /categories debe retornar lista de categorias")
    void listCategoriesShouldReturnAll() {
        var categories = List.of(
                CategoryDTO.builder().id(UUID.randomUUID()).name("Cat1").slug("cat1").build(),
                CategoryDTO.builder().id(UUID.randomUUID()).name("Cat2").slug("cat2").build());
        when(categoryService.listAll()).thenReturn(categories);

        var response = categoryController.listCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody().getData()).size());
    }

    @Test
    @DisplayName("POST /categories debe crear categoria")
    void createCategoryShouldSucceed() {
        var dto = CategoryDTO.builder()
                .id(UUID.randomUUID())
                .name("New Category")
                .slug("new-category")
                .build();
        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(dto);

        var request = new CreateCategoryRequest();
        request.setName("New Category");
        request.setImageUrl("https://img.com/img.jpg");

        var response = categoryController.createCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Category", ((CategoryDTO) response.getBody().getData()).getName());
    }
}