package com.tuapp.marketplace.users.controller;

import com.tuapp.marketplace.auth.entity.User;
import com.tuapp.marketplace.shared.response.ApiResponse;
import com.tuapp.marketplace.users.dto.UpdateUserRequest;
import com.tuapp.marketplace.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de perfil de usuario.
 * Permite consultar y actualizar los datos personales del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserAPI {

    private final UserService userService;

    /**
     * Devuelve los datos del perfil del usuario autenticado.
     *
     * @param user Usuario autenticado (inyectado por Spring Security)
     * @return 200 OK con datos del perfil
     */
    @GetMapping("/me")
    @Override
    public ResponseEntity<ApiResponse<?>> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(user)));
    }

    /**
     * Actualiza los datos del perfil del usuario autenticado.
     *
     * @param user    Usuario autenticado
     * @param request Campos a actualizar (nombre y/o telefono)
     * @return 200 OK con perfil actualizado
     */
    @PutMapping("/me")
    @Override
    public ResponseEntity<ApiResponse<?>> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateMyProfile(user, request)));
    }
}
