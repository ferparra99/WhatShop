package com.whatshop.marketplace.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Campos sensibles en toString")
class SensitiveToStringTest {

    @Test
    @DisplayName("RegisterRequest.toString no debe exponer la contraseña")
    void registerRequestShouldHidePassword() {
        var req = new RegisterRequest();
        req.setPassword("miClaveSecreta123");

        assertFalse(req.toString().contains("miClaveSecreta123"));
    }

    @Test
    @DisplayName("LoginRequest.toString no debe exponer la contraseña")
    void loginRequestShouldHidePassword() {
        var req = new LoginRequest();
        req.setPassword("miClaveSecreta123");

        assertFalse(req.toString().contains("miClaveSecreta123"));
    }

    @Test
    @DisplayName("User.toString no debe exponer el hash de contraseña")
    void userShouldHidePassword() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .password("$2a$10$hashBcryptSecreto")
                .fullName("Test User")
                .role(Role.ROLE_BUYER)
                .build();

        assertFalse(user.toString().contains("$2a$10$hashBcryptSecreto"));
    }
}