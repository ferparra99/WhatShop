package com.whatshop.marketplace.products.controller;

import com.whatshop.marketplace.products.dto.CreateCategoryRequest;
import com.whatshop.marketplace.products.service.CategoryService;
import com.whatshop.marketplace.products.swaggerdoc.CategoryAPI;
import com.whatshop.marketplace.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de categorias.
 * Permite listar y crear categorias para clasificar productos.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {

    private final CategoryService categoryService;

    /**
     * Lista todas las categorias disponibles.
     *
     * @return 200 OK con listado de categorias
     */
    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<?>> listCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.listAll()));
    }

    /**
     * Crea una nueva categoria. El slug se genera automaticamente a partir del nombre.
     *
     * @param request Datos de la categoria (name obligatorio, imageUrl opcional)
     * @return 201 CREATED con la categoria creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        var category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(category, "Categoria creada exitosamente"));
    }
}