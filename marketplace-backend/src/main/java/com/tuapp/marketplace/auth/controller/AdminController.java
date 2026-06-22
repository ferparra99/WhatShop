package com.tuapp.marketplace.auth.controller;

import com.tuapp.marketplace.shared.response.ApiResponse;
import com.tuapp.marketplace.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador de administracion.
 * Solo accesible para usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController implements AdminAPI {

    private final UserService userService;

    /**
     * Lista todos los usuarios registrados en el sistema.
     *
     * @return 200 OK con listado completo de usuarios
     */
    @GetMapping("/users")
    @Override
    public ResponseEntity<ApiResponse<?>> listUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.listAllUsers()));
    }

    /**
     * Activa o desactiva un usuario por su ID.
     * Un usuario desactivado no puede iniciar sesion.
     *
     * @param id ID del usuario a modificar
     * @return 200 OK con mensaje de confirmacion
     * @throws com.tuapp.marketplace.shared.exception.ResourceNotFoundException si el usuario no existe
     */
    @PutMapping("/users/{id}/enable")
    @Override
    public ResponseEntity<ApiResponse<?>> toggleUserEnabled(@PathVariable UUID id) {
        userService.toggleUserEnabled(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Estado de usuario actualizado"));
    }
}
