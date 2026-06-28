package com.whatshop.marketplace.auth.swaggerdoc;

import com.whatshop.marketplace.auth.dto.LoginRequest;
import com.whatshop.marketplace.auth.dto.RegisterRequest;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
            - **SELLER**: puede gestionar su tienda y productos *(requiere store.storeName)*
            - **ADMIN**: acceso total al sistema *(store opcional)*

            **Ejemplo SELLER:**
            ```json
            {
              "email": "vendedor@example.com",
              "password": "Vendedor123",
              "fullName": "Maria Gonzalez",
              "phone": "+573109876543",
              "role": "SELLER",
              "store": {
                "storeName": "Tienda de Maria",
                "description": "Productos artesanales",
                "nit": "900123456-7",
                "logoUrl": "https://ejemplo.com/logo.png"
              }
            }
            ```

            Devuelve un token JWT para autenticarse automaticamente.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos invalidos (email duplicado, rol incorrecto, etc.)", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "Iniciar sesion", description = """
            Autentica al usuario con email y contrasena.

            **Respuesta:** devuelve un token JWT que debe usarse en el header
            `Authorization: Bearer <token>` para acceder a los endpoints protegidos.
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso - token JWT generado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales invalidas", content = @Content)
    })
    ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "Ver mi perfil", description = "Devuelve los datos del usuario autenticado. Requiere token JWT valido.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil del usuario actual"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token invalido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> me(@org.springframework.security.core.annotation.AuthenticationPrincipal User user);

    @Operation(summary = "Renovar token JWT", description = "Genera un nuevo token JWT para el usuario autenticado. Util cuando el token actual esta por expirar.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token invalido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    ResponseEntity<ApiResponse<?>> refresh(@org.springframework.security.core.annotation.AuthenticationPrincipal User user);
}
