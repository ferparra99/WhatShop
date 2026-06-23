package com.whatshop.marketplace.products.swaggerdoc;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Products", description = "Catalogo de productos - listar, filtrar, crear, editar y eliminar")
public interface ProductAPI {

    @Operation(summary = "Listar productos activos", description = """
            Devuelve los productos activos con filtros opcionales y paginacion.

            **Filtros disponibles:**
            - `categoryId` - filtrar por categoria
            - `minPrice` / `maxPrice` - filtrar por rango de precio
            - `sellerId` - filtrar por vendedor
            - `search` - busqueda por nombre del producto
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado de productos")
    })
    ResponseEntity<ApiResponse<?>> listProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) String search,
            Pageable pageable);

    @Operation(summary = "Ver detalle de un producto", description = "Devuelve la informacion completa de un producto especifico.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalle del producto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> getProduct(@PathVariable UUID id);

    @Operation(summary = "Buscar productos por nombre", description = "Busca productos cuyo nombre contenga el texto indicado.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resultados de la busqueda")
    })
    ResponseEntity<ApiResponse<?>> searchProducts(@RequestParam String q, Pageable pageable);

    @Operation(summary = "Crear un producto", description = "Crea un nuevo producto. Requiere rol **SELLER**. El vendedor debe tener un perfil de tienda creado previamente.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos invalidos o perfil de vendedor inexistente", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> createProduct(
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            @Valid @RequestBody CreateProductRequest request);

    @Operation(summary = "Editar un producto", description = "Actualiza los datos de un producto existente. Solo el vendedor propietario puede editarlo. Requiere rol **SELLER**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No podes editar un producto que no te pertenece", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable UUID id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProductRequest request);

    @Operation(summary = "Eliminar un producto (soft delete)", description = "Marca un producto como eliminado (no se borra de la base de datos). Solo el vendedor propietario puede eliminar sus productos. Requiere rol **SELLER**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto eliminado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "No podes eliminar un producto que no te pertenece", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> deleteProduct(
            @PathVariable UUID id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user);
}
