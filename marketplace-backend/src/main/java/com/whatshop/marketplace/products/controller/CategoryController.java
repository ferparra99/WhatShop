package com.whatshop.marketplace.products.controller;

import com.whatshop.marketplace.products.service.CategoryService;
import com.whatshop.marketplace.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * @param body Mapa con "name" (obligatorio) e "imageUrl" (opcional)
     * @return 201 CREATED con la categoria creada
     */
    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody Map<String, String> body) {
        var name = body.get("name");
        var imageUrl = body.get("imageUrl");
        var category = categoryService.create(name, imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(category, "Categoria creada exitosamente"));
    }
}
