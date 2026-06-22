package com.whatshop.marketplace.products.controller;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import com.whatshop.marketplace.products.service.ProductService;
import com.whatshop.marketplace.shared.response.ApiResponse;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controlador de productos.
 * Permite listar, buscar, crear, editar y eliminar productos del catalogo.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController implements ProductAPI {

    private final ProductService productService;

    /**
     * Lista productos activos con filtros opcionales y paginacion.
     *
     * @param categoryId Filtro por categoria (opcional)
     * @param minPrice   Precio minimo (opcional)
     * @param maxPrice   Precio maximo (opcional)
     * @param sellerId   Filtro por vendedor (opcional)
     * @param search     Busqueda por nombre (opcional)
     * @param pageable   Parametros de paginacion
     * @return 200 OK con listado paginado de productos
     */
    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<?>> listProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.listProducts(categoryId, minPrice, maxPrice, sellerId, search, pageable)));
    }

    /**
     * Devuelve el detalle completo de un producto por su ID.
     *
     * @param id ID del producto
     * @return 200 OK con detalle del producto
     * @throws ResourceNotFoundException si no existe
     */
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<?>> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    /**
     * Busca productos cuyo nombre contenga el texto indicado.
     *
     * @param q        Texto de busqueda
     * @param pageable Parametros de paginacion
     * @return 200 OK con resultados paginados
     */
    @GetMapping("/search")
    @Override
    public ResponseEntity<ApiResponse<?>> searchProducts(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.listProducts(null, null, null, null, q, pageable)));
    }

    /**
     * Crea un nuevo producto. Requiere rol SELLER con perfil de tienda.
     *
     * @param user    Vendedor autenticado
     * @param request Datos del producto (categoria, nombre, precio, stock)
     * @return 201 CREATED con el producto creado
     */
    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<?>> createProduct(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateProductRequest request) {
        var product = productService.createProduct(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Producto creado exitosamente"));
    }

    /**
     * Actualiza los datos de un producto existente.
     * Solo el vendedor propietario puede editar sus productos.
     *
     * @param id      ID del producto a editar
     * @param user    Vendedor autenticado (debe ser el propietario)
     * @param request Campos a actualizar
     * @return 200 OK con producto actualizado
     */
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.updateProduct(id, user.getId(), request), "Producto actualizado exitosamente"));
    }

    /**
     * Elimina un producto (soft delete). Lo marca como DELETED sin borrarlo de la BD.
     * Solo el vendedor propietario puede eliminar sus productos.
     *
     * @param id   ID del producto a eliminar
     * @param user Vendedor autenticado (debe ser el propietario)
     * @return 200 OK con mensaje de confirmacion
     */
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<?>> deleteProduct(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        productService.deleteProduct(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
    }
}
