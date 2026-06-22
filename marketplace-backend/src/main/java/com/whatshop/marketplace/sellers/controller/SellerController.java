package com.whatshop.marketplace.sellers.controller;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.service.ProductService;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;
import com.whatshop.marketplace.sellers.service.SellerService;
import com.whatshop.marketplace.shared.response.ApiResponse;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador de vendedores.
 * Permite consultar perfiles publicos y gestionar la tienda del vendedor autenticado.
 */
@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController implements SellerAPI {

    private final SellerService sellerService;
    private final ProductService productService;

    /**
     * Lista todos los vendedores con tienda activa en el marketplace.
     *
     * @return 200 OK con listado de vendedores
     */
    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<?>> listActiveSellers() {
        return ResponseEntity.ok(ApiResponse.success(sellerService.listActiveSellers()));
    }

    /**
     * Devuelve el perfil publico de un vendedor por su ID.
     *
     * @param id ID del vendedor
     * @return 200 OK con perfil del vendedor
     * @throws ResourceNotFoundException si no existe
     */
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<?>> getSellerById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getSellerById(id)));
    }

    /**
     * Devuelve el perfil completo de la tienda del vendedor autenticado.
     *
     * @param user Vendedor autenticado
     * @return 200 OK con perfil de la tienda
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    @Override
    public ResponseEntity<ApiResponse<?>> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.getMyProfile(user.getId())));
    }

    /**
     * Actualiza los datos de la tienda del vendedor autenticado.
     *
     * @param user    Vendedor autenticado
     * @param request Campos a actualizar (storeName, description, nit, logoUrl)
     * @return 200 OK con tienda actualizada
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    @Override
    public ResponseEntity<ApiResponse<?>> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateSellerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(sellerService.updateMyProfile(user.getId(), request)));
    }

    /**
     * Lista los productos del vendedor autenticado con paginacion.
     *
     * @param user     Vendedor autenticado
     * @param pageable Parametros de paginacion (page, size, sort)
     * @return 200 OK con listado paginado de productos
     */
    @GetMapping("/me/products")
    @PreAuthorize("hasRole('SELLER')")
    @Override
    public ResponseEntity<ApiResponse<?>> listMyProducts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.listMyProducts(user.getId(), pageable)));
    }
}
