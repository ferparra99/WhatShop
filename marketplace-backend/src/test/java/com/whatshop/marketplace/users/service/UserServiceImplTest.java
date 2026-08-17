package com.whatshop.marketplace.users.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import com.whatshop.marketplace.users.dto.UpdateUserRequest;
import com.whatshop.marketplace.users.dto.UserProfileDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("user@test.com")
                .fullName("Original Name")
                .phone("+573001234567")
                .role(Role.ROLE_BUYER)
                .enabled(true)
                .build();
    }

    @Nested
    @DisplayName("getMyProfile")
    class GetMyProfile {

        @Test
        @DisplayName("debe retornar el perfil del usuario")
        void shouldReturnMyProfile() {
            var result = userService.getMyProfile(user);

            assertEquals(userId, result.getId());
            assertEquals("user@test.com", result.getEmail());
            assertEquals("Original Name", result.getFullName());
            assertEquals(Role.ROLE_BUYER, result.getRole());
            assertTrue(result.isEnabled());
        }
    }

    @Nested
    @DisplayName("updateMyProfile")
    class UpdateMyProfile {

        @Test
        @DisplayName("debe actualizar solo el fullName")
        void shouldUpdateFullNameOnly() {
            var request = new UpdateUserRequest();
            request.setFullName("New Name");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = userService.updateMyProfile(user, request);

            assertEquals("New Name", result.getFullName());
            assertEquals("+573001234567", result.getPhone()); // unchanged
        }

        @Test
        @DisplayName("debe actualizar solo el phone")
        void shouldUpdatePhoneOnly() {
            var request = new UpdateUserRequest();
            request.setPhone("+573009876543");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = userService.updateMyProfile(user, request);

            assertEquals("Original Name", result.getFullName()); // unchanged
            assertEquals("+573009876543", result.getPhone());
        }

        @Test
        @DisplayName("debe actualizar todos los campos")
        void shouldUpdateAllFields() {
            var request = new UpdateUserRequest();
            request.setFullName("New Name");
            request.setPhone("+573009876543");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = userService.updateMyProfile(user, request);

            assertEquals("New Name", result.getFullName());
            assertEquals("+573009876543", result.getPhone());
        }

        @Test
        @DisplayName("no debe cambiar nada si no se envian campos")
        void shouldNotChangeAnythingWhenNoFields() {
            var request = new UpdateUserRequest();
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = userService.updateMyProfile(user, request);

            assertEquals("Original Name", result.getFullName());
            assertEquals("+573001234567", result.getPhone());
        }
    }

    @Nested
    @DisplayName("listAllUsers")
    class ListAllUsers {

        @Test
        @DisplayName("debe retornar todos los usuarios")
        void shouldReturnAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(user));

            List<UserProfileDTO> result = userService.listAllUsers();

            assertEquals(1, result.size());
            assertEquals("user@test.com", result.get(0).getEmail());
        }
    }

    @Nested
    @DisplayName("toggleUserEnabled")
    class ToggleUserEnabled {

        @Test
        @DisplayName("debe deshabilitar un usuario habilitado")
        void shouldDisableEnabledUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.toggleUserEnabled(userId);

            assertFalse(user.isEnabled());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("debe habilitar un usuario deshabilitado")
        void shouldEnableDisabledUser() {
            user.setEnabled(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.toggleUserEnabled(userId);

            assertTrue(user.isEnabled());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("debe lanzar excepcion si el usuario no existe")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.toggleUserEnabled(UUID.randomUUID()));
            verify(userRepository, never()).save(any());
        }
    }
}
