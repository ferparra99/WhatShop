package com.whatshop.marketplace.auth.swaggerdoc;

import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Admin", description = "Administracion del sistema - solo accesible con rol ADMIN")
@SecurityRequirement(name = "bearer-jwt")
public interface AdminAPI {

    @Operation(summary = "Listar todos los usuarios", description = "Devuelve el listado completo de usuarios registrados en el sistema. Solo accesible para usuarios con rol **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de usuarios"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenes permisos de administrador", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> listUsers();

    @Operation(summary = "Activar / desactivar usuario", description = "Cambia el estado de un usuario (activo/inactivo). Un usuario desactivado no puede iniciar sesion. Solo accesible para usuarios con rol **ADMIN**.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> toggleUserEnabled(@PathVariable UUID id);
}
