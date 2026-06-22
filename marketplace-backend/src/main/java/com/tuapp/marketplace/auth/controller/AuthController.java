package com.tuapp.marketplace.auth.controller;

import com.tuapp.marketplace.auth.dto.LoginRequest;
import com.tuapp.marketplace.auth.dto.RegisterRequest;
import com.tuapp.marketplace.auth.entity.User;
import com.tuapp.marketplace.auth.service.AuthService;
import com.tuapp.marketplace.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticacion.
 * Maneja registro, inicio de sesion, consulta de perfil y renovacion de tokens JWT.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro (email, password, nombre, telefono, rol)
     * @return 201 CREATED con token JWT + datos del usuario
     * @throws com.tuapp.marketplace.shared.exception.BadRequestException si el email ya existe o el rol es invalido
     */
    @PostMapping("/register")
    @Override
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Autentica al usuario con email y contrasena.
     *
     * @param request Credenciales (email y password)
     * @return 200 OK con token JWT + datos del usuario
     * @throws org.springframework.security.authentication.BadCredentialsException si las credenciales son invalidas
     */
    @PostMapping("/login")
    @Override
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Devuelve los datos del usuario autenticado mediante su token JWT.
     *
     * @param user Usuario autenticado (inyectado por Spring Security)
     * @return 200 OK con perfil del usuario
     */
    @GetMapping("/me")
    @Override
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.me(user));
    }

    /**
     * Genera un nuevo token JWT para el usuario autenticado.
     * Util cuando el token actual esta por expirar.
     *
     * @param user Usuario autenticado (inyectado por Spring Security)
     * @return 200 OK con el nuevo token JWT
     */
    @PostMapping("/refresh")
    @Override
    public ResponseEntity<ApiResponse<?>> refresh(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.refresh(user));
    }
}
