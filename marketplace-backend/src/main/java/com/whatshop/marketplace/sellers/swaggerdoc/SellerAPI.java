package com.whatshop.marketplace.sellers.swaggerdoc;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;
import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Sellers", description = "Perfil del vendedor - gestion de tienda y productos propios")
public interface SellerAPI {

    @Operation(summary = "Listar vendedores activos", description = "Devuelve todos los vendedores que tienen su tienda activa en el marketplace.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de vendedores")
    })
    ResponseEntity<ApiResponse<?>> listActiveSellers();

    @Operation(summary = "Ver perfil publico de un vendedor", description = "Devuelve la informacion publica de la tienda de un vendedor especifico.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil del vendedor"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendedor no encontrado", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> getSellerById(@PathVariable UUID id);

    @Operation(summary = "Ver mi perfil de vendedor", description = "Devuelve el perfil completo de la tienda del vendedor autenticado. Requiere rol **SELLER** o **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil del vendedor"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenes rol SELLER", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil de vendedor no encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> getMyProfile(@org.springframework.security.core.annotation.AuthenticationPrincipal User user);

    @Operation(summary = "Actualizar mi tienda", description = "Actualiza los datos de la tienda del vendedor autenticado (nombre, descripcion, NIT, logo). Requiere rol **SELLER** o **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tienda actualizada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos invalidos o nombre de tienda duplicado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> updateMyProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateSellerRequest request);

    @Operation(summary = "Listar mis productos", description = "Devuelve los productos del vendedor autenticado con paginacion. Requiere rol **SELLER** o **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado paginado de mis productos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenes rol SELLER", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> listMyProducts(
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            Pageable pageable);
}
