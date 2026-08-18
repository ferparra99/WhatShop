package com.whatshop.marketplace.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.auth.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SecurityConfig (integración)")
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    private String tokenFor(User user) {
        return "Bearer " + jwtUtil.generateToken(user.getId(), user.getEmail());
    }

    private User saveUser(Role role, boolean enabled) {
        return userRepository.save(User.builder()
                .email(role.name().toLowerCase() + enabled + "@test.com")
                .password("$2a$10$hashBcryptDummyValue000000000000000000000000000000")
                .fullName("Test User")
                .phone("+573001234567")
                .role(role)
                .enabled(enabled)
                .build());
    }

    @Test
    @DisplayName("GET /products es público (sin token retorna 200)")
    void productsArePublic() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /auth/me sin token retorna 401 con envelope JSON")
    void meWithoutTokenReturns401Envelope() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("GET /sellers/me sin token retorna 401 (no 500)")
    void sellersMeWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/sellers/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("token de usuario deshabilitado es revocado (401)")
    void disabledUserTokenIsRevoked() throws Exception {
        var user = saveUser(Role.ROLE_BUYER, false);

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", tokenFor(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("BUYER intentando acceder a /admin/users retorna 403 con envelope JSON")
    void buyerCannotAccessAdminEndpoints() throws Exception {
        var buyer = saveUser(Role.ROLE_BUYER, true);

        mockMvc.perform(get("/api/v1/admin/users").header("Authorization", tokenFor(buyer)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("BUYER intentando crear categoría retorna 403")
    void buyerCannotCreateCategory() throws Exception {
        var buyer = saveUser(Role.ROLE_BUYER, true);

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", tokenFor(buyer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nueva\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}