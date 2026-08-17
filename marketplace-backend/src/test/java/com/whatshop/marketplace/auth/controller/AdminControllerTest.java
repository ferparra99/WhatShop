package com.whatshop.marketplace.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.shared.response.ApiResponse;
import com.whatshop.marketplace.users.dto.UserProfileDTO;
import com.whatshop.marketplace.users.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController")
class AdminControllerTest {

    @Mock
    private UserService userService;

    private AdminController adminController;

    @BeforeEach
    void setUp() {
        adminController = new AdminController(userService);
    }

    @Test
    @DisplayName("GET /users debe retornar lista completa de usuarios")
    void listUsersShouldReturnAllUsers() {
        var userDto = UserProfileDTO.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .fullName("Test")
                .role(Role.ROLE_BUYER)
                .enabled(true)
                .build();
        when(userService.listAllUsers()).thenReturn(List.of(userDto));

        var response = adminController.listUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody().getData()).size());
    }

    @Test
    @DisplayName("PUT /users/{id}/enable debe alternar estado del usuario")
    void toggleUserEnabledShouldSucceed() {
        var userId = UUID.randomUUID();
        doNothing().when(userService).toggleUserEnabled(userId);

        var response = adminController.toggleUserEnabled(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).toggleUserEnabled(userId);
    }
}
