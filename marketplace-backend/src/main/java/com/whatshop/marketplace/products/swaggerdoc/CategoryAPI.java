package com.whatshop.marketplace.products.swaggerdoc;

import com.whatshop.marketplace.products.dto.CreateCategoryRequest;
import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Categories", description = "Categorias de productos - listar y crear")
public interface CategoryAPI {

    @Operation(summary = "Listar todas las categorias", description = "Devuelve el listado completo de categorias disponibles para clasificar productos.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorias")
    })
    ResponseEntity<ApiResponse<?>> listCategories();

    @Operation(summary = "Crear una nueva categoria", description = "Crea una categoria nueva. El slug se genera automaticamente a partir del nombre. Requiere rol **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoria creada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La categoria ya existe", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CreateCategoryRequest request);
}