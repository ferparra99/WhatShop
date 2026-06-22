package com.tuapp.marketplace.auth.controller;

import com.tuapp.marketplace.auth.dto.LoginRequest;
import com.tuapp.marketplace.auth.dto.RegisterRequest;
import com.tuapp.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Registro, inicio de sesion y gestion de tokens JWT")
public interface AuthAPI {

    @Operation(summary = "Registrar nuevo usuario", description = """
            Crea una cuenta nueva en el sistema. El rol determina los permisos:
            - **BUYER**: puede navegar y comprar productos
            - **SELLER**: puede gestionar su tienda y productos
            - **ADMIN**: acceso total al sistema

            Devuelve un token JWT para autenticarse automaticamente.
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos (email duplicado, rol incorrecto, etc.)", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "Iniciar sesion", description = """
            Autentica al usuario con email y contrasena.

            **Respuesta:** devuelve un token JWT que debe usarse en el header
            `Authorization: Bearer <token>` para acceder a los endpoints protegidos.
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso - token JWT generado"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "Ver mi perfil", description = "Devuelve los datos del usuario autenticado. Requiere token JWT valido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil del usuario actual"),
        @ApiResponse(responseCode = "401", description = "Token invalido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> me(org.springframework.security.core.annotation.AuthenticationPrincipal User user);

    @Operation(summary = "Renovar token JWT", description = "Genera un nuevo token JWT para el usuario autenticado. Util cuando el token actual esta por expirar.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token invalido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> refresh(org.springframework.security.core.annotation.AuthenticationPrincipal User user);
}
