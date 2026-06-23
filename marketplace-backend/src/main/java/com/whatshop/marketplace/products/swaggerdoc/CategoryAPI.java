package com.whatshop.marketplace.products.swaggerdoc;

import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name = "Categories", description = "Categorias de productos - listar y crear")
public interface CategoryAPI {

    @Operation(summary = "Listar todas las categorias", description = "Devuelve el listado completo de categorias disponibles para clasificar productos.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorias")
    })
    ResponseEntity<ApiResponse<?>> listCategories();

    @Operation(summary = "Crear una nueva categoria", description = "Crea una categoria nueva. El slug se genera automaticamente a partir del nombre.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoria creada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La categoria ya existe", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> createCategory(Map<String, String> body);
}
