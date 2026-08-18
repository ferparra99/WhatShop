package com.whatshop.marketplace.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.dto.AuthResponse;
import com.whatshop.marketplace.auth.dto.LoginRequest;
import com.whatshop.marketplace.auth.dto.RegisterRequest;
import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.service.AuthService;
import com.whatshop.marketplace.shared.response.ApiResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    @DisplayName("POST /register debe retornar 201")
    void registerShouldReturn201() {
        var request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("Pass123");
        request.setFullName("Test");
        request.setPhone("+573001234567");
        request.setRole("BUYER");

        var authResponse = AuthResponse.builder()
                .token("mock-token")
                .userId(UUID.randomUUID())
                .email("test@test.com")
                .role(Role.ROLE_BUYER)
                .build();
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(ApiResponse.success(authResponse, "Usuario registrado exitosamente"));

        var response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("mock-token", ((AuthResponse) response.getBody().getData()).getToken());
    }

    @Test
    @DisplayName("POST /login debe retornar 200")
    void loginShouldReturn200() {
        var request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("Pass123");

        var authResponse = AuthResponse.builder()
                .token("mock-token")
                .userId(UUID.randomUUID())
                .email("test@test.com")
                .role(Role.ROLE_BUYER)
                .build();
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(ApiResponse.success(authResponse, "Inicio de sesion exitoso"));

        var response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("mock-token", ((AuthResponse) response.getBody().getData()).getToken());
    }

    @Test
    @DisplayName("GET /me debe retornar 200")
    void meShouldReturn200() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .fullName("Test")
                .role(Role.ROLE_BUYER)
                .build();
        var authResponse = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
        when(authService.me(any(User.class)))
                .thenReturn(ApiResponse.success(authResponse));

        var response = authController.me(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), ((AuthResponse) response.getBody().getData()).getUserId());
    }

    @Test
    @DisplayName("POST /refresh debe retornar 200")
    void refreshShouldReturn200() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .build();
        var authResponse = AuthResponse.builder()
                .token("new-token")
                .build();
        when(authService.refresh(any(User.class)))
                .thenReturn(ApiResponse.success(authResponse, "Token renovado exitosamente"));

        var response = authController.refresh(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new-token", ((AuthResponse) response.getBody().getData()).getToken());
    }
}
