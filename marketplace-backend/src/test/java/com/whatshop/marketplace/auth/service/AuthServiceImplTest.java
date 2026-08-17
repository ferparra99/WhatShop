package com.whatshop.marketplace.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.dto.AuthResponse;
import com.whatshop.marketplace.auth.dto.LoginRequest;
import com.whatshop.marketplace.auth.dto.RegisterRequest;
import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.sellers.service.SellerService;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SellerService sellerService;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil,
                authenticationManager, sellerService);
    }

    private User createUser(UUID id, String email, Role role) {
        return User.builder()
                .id(id)
                .email(email)
                .password(passwordEncoder.encode("Pass123"))
                .fullName("Test User")
                .phone("+573001234567")
                .role(role)
                .enabled(true)
                .build();
    }

    private RegisterRequest createRegisterRequest(String email, String role, String storeName) {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword("Pass123");
        req.setFullName("Test User");
        req.setPhone("+573001234567");
        req.setRole(role);
        if (storeName != null) {
            RegisterRequest.StoreInfo store = new RegisterRequest.StoreInfo();
            store.setStoreName(storeName);
            store.setDescription("Test store");
            store.setNit("123456-7");
            store.setLogoUrl("https://example.com/logo.png");
            req.setStore(store);
        }
        return req;
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("debe registrar un BUYER exitosamente y retornar token")
        void shouldRegisterBuyerSuccessfully() {
            var req = createRegisterRequest("buyer@test.com", "BUYER", null);
            var userId = UUID.randomUUID();
            when(userRepository.existsByEmail("buyer@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(userId);
                return u;
            });
            when(jwtUtil.generateToken(userId, "buyer@test.com")).thenReturn("mock-token");

            var response = authService.register(req);

            assertTrue(response.isSuccess());
            assertNotNull(response.getData());
            assertEquals("mock-token", response.getData().getToken());
            assertEquals(userId, response.getData().getUserId());
            assertEquals(Role.ROLE_BUYER, response.getData().getRole());
            verify(userRepository).save(any(User.class));
            verify(sellerService, never()).createSellerProfile(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("debe registrar un SELLER con tienda exitosamente")
        void shouldRegisterSellerWithStoreSuccessfully() {
            var req = createRegisterRequest("seller@test.com", "SELLER", "Mi Tienda");
            var userId = UUID.randomUUID();
            when(userRepository.existsByEmail("seller@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(userId);
                return u;
            });
            when(jwtUtil.generateToken(userId, "seller@test.com")).thenReturn("mock-token");

            var response = authService.register(req);

            assertTrue(response.isSuccess());
            assertEquals("mock-token", response.getData().getToken());
            verify(sellerService).createSellerProfile(any(User.class), eq("Mi Tienda"),
                    eq("Test store"), eq("123456-7"), eq("https://example.com/logo.png"));
        }

        @Test
        @DisplayName("debe registrar un ADMIN con tienda exitosamente")
        void shouldRegisterAdminWithStoreSuccessfully() {
            var req = createRegisterRequest("admin@test.com", "ADMIN", "Admin Store");
            var userId = UUID.randomUUID();
            when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(userId);
                return u;
            });
            when(jwtUtil.generateToken(userId, "admin@test.com")).thenReturn("mock-token");

            var response = authService.register(req);

            assertTrue(response.isSuccess());
            verify(sellerService).createSellerProfile(any(User.class), eq("Admin Store"), any(), any(), any());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el email ya existe")
        void shouldThrowWhenEmailAlreadyExists() {
            var req = createRegisterRequest("exist@test.com", "BUYER", null);
            when(userRepository.existsByEmail("exist@test.com")).thenReturn(true);

            assertThrows(BadRequestException.class, () -> authService.register(req));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepcion para rol invalido")
        void shouldThrowForInvalidRole() {
            var req = createRegisterRequest("bad@test.com", "INVALID", null);

            assertThrows(BadRequestException.class, () -> authService.register(req));
        }

        @Test
        @DisplayName("debe lanzar excepcion si SELLER no proporciona tienda")
        void shouldThrowWhenSellerHasNoStore() {
            var req = createRegisterRequest("seller@test.com", "SELLER", null);

            assertThrows(BadRequestException.class, () -> authService.register(req));
        }

        @Test
        @DisplayName("debe lanzar excepcion si ADMIN no proporciona tienda")
        void shouldThrowWhenAdminHasNoStore() {
            var req = createRegisterRequest("admin@test.com", "ADMIN", null);

            assertThrows(BadRequestException.class, () -> authService.register(req));
        }

        @Test
        @DisplayName("debe lanzar excepcion si SELLER tiene storeName vacio")
        void shouldThrowWhenSellerStoreNameIsBlank() {
            var req = createRegisterRequest("seller@test.com", "SELLER", "   ");

            assertThrows(BadRequestException.class, () -> authService.register(req));
        }

        @Test
        @DisplayName("debe codificar la contrasena con BCrypt")
        void shouldEncodePassword() {
            var req = createRegisterRequest("buyer@test.com", "BUYER", null);
            when(userRepository.existsByEmail("buyer@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(UUID.randomUUID());
                return u;
            });
            when(jwtUtil.generateToken(any(), any())).thenReturn("token");

            authService.register(req);

            ArgumentCaptor<User> captor = ArgumentCaptor.captor();
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertTrue(passwordEncoder.matches("Pass123", saved.getPassword()));
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("debe autenticar y retornar token para credenciales correctas")
        void shouldLoginSuccessfully() {
            var req = new LoginRequest();
            req.setEmail("user@test.com");
            req.setPassword("Pass123");
            var user = createUser(UUID.randomUUID(), "user@test.com", Role.ROLE_BUYER);
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("mock-token");

            var response = authService.login(req);

            assertTrue(response.isSuccess());
            assertEquals("mock-token", response.getData().getToken());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("debe lanzar excepcion si el usuario no existe")
        void shouldThrowWhenUserNotFound() {
            var req = new LoginRequest();
            req.setEmail("noexist@test.com");
            req.setPassword("Pass123");
            doThrow(BadCredentialsException.class).when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            assertThrows(BadCredentialsException.class, () -> authService.login(req));
        }
    }

    @Nested
    @DisplayName("me")
    class Me {

        @Test
        @DisplayName("debe retornar los datos del usuario autenticado")
        void shouldReturnAuthenticatedUserData() {
            var user = createUser(UUID.randomUUID(), "user@test.com", Role.ROLE_BUYER);

            var response = authService.me(user);

            assertTrue(response.isSuccess());
            assertEquals(user.getId(), response.getData().getUserId());
            assertEquals(user.getEmail(), response.getData().getEmail());
            assertEquals(user.getFullName(), response.getData().getFullName());
            assertEquals(user.getRole(), response.getData().getRole());
        }
    }

    @Nested
    @DisplayName("refresh")
    class Refresh {

        @Test
        @DisplayName("debe generar un nuevo token")
        void shouldGenerateNewToken() {
            var user = createUser(UUID.randomUUID(), "user@test.com", Role.ROLE_BUYER);
            when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("refreshed-token");

            var response = authService.refresh(user);

            assertTrue(response.isSuccess());
            assertEquals("refreshed-token", response.getData().getToken());
        }
    }
}
