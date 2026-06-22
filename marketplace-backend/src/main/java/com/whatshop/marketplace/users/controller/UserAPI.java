package com.whatshop.marketplace.users.controller;

import com.whatshop.marketplace.shared.response.ApiResponse;
import com.whatshop.marketplace.users.dto.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Users", description = "Perfil del comprador - consultar y actualizar datos personales")
@SecurityRequirement(name = "bearer-jwt")
public interface UserAPI {

    @Operation(summary = "Ver mi perfil de usuario", description = "Devuelve los datos personales del usuario autenticado (email, nombre, telefono, rol).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> getMyProfile(org.springframework.security.core.annotation.AuthenticationPrincipal User user);

    @Operation(summary = "Actualizar mi perfil", description = "Actualiza los datos personales del usuario autenticado (nombre y/o telefono).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> updateMyProfile(
            org.springframework.security.core.annotation.AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request);
}
