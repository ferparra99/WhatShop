package com.whatshop.marketplace.users.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.shared.response.ApiResponse;
import com.whatshop.marketplace.users.dto.UpdateUserRequest;
import com.whatshop.marketplace.users.dto.UserProfileDTO;
import com.whatshop.marketplace.users.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    private User user;
    private UserProfileDTO userDto;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        var userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("user@test.com")
                .fullName("Test User")
                .phone("+573001234567")
                .role(Role.ROLE_BUYER)
                .build();
        userDto = UserProfileDTO.builder()
                .id(userId)
                .email("user@test.com")
                .fullName("Test User")
                .phone("+573001234567")
                .role(Role.ROLE_BUYER)
                .build();
    }

    @Test
    @DisplayName("GET /me debe retornar perfil del usuario autenticado")
    void getMyProfileShouldReturnProfile() {
        when(userService.getMyProfile(any(User.class))).thenReturn(userDto);

        var response = userController.getMyProfile(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user@test.com", ((UserProfileDTO) response.getBody().getData()).getEmail());
    }

    @Test
    @DisplayName("PUT /me debe actualizar y retornar perfil")
    void updateMyProfileShouldReturnUpdatedProfile() {
        var request = new UpdateUserRequest();
        request.setFullName("Updated Name");
        var updatedDto = UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName("Updated Name")
                .phone(user.getPhone())
                .role(Role.ROLE_BUYER)
                .build();
        when(userService.updateMyProfile(any(User.class), any(UpdateUserRequest.class)))
                .thenReturn(updatedDto);

        var response = userController.updateMyProfile(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", ((UserProfileDTO) response.getBody().getData()).getFullName());
    }
}
