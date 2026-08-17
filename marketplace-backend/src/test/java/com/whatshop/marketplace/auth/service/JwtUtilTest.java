package com.whatshop.marketplace.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private static final String SECRET = "my-super-secret-key-that-is-at-least-256-bits-long-for-testing-12345";
    private static final long EXPIRATION = 3600000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("debe generar un token valido para un usuario valido")
        void shouldGenerateValidToken() {
            var userId = UUID.randomUUID();
            var token = jwtUtil.generateToken(userId, "test@example.com");

            assertNotNull(token);
            assertFalse(token.isBlank());
        }
    }

    @Nested
    @DisplayName("extractUserId")
    class ExtractUserId {

        @Test
        @DisplayName("debe extraer el userId del token generado")
        void shouldExtractUserIdFromToken() {
            var userId = UUID.randomUUID();
            var token = jwtUtil.generateToken(userId, "test@example.com");

            var extractedId = jwtUtil.extractUserId(token);

            assertEquals(userId.toString(), extractedId);
        }
    }

    @Nested
    @DisplayName("isValid")
    class IsValid {

        @Test
        @DisplayName("debe retornar true para un token valido")
        void shouldReturnTrueForValidToken() {
            var token = jwtUtil.generateToken(UUID.randomUUID(), "test@example.com");

            assertTrue(jwtUtil.isValid(token));
        }

        @Test
        @DisplayName("debe retornar false para un token mal formado")
        void shouldReturnFalseForMalformedToken() {
            assertFalse(jwtUtil.isValid("invalid-token-string"));
        }

        @Test
        @DisplayName("debe retornar false para un token vacio")
        void shouldReturnFalseForEmptyToken() {
            assertFalse(jwtUtil.isValid(""));
        }

        @Test
        @DisplayName("debe retornar false para un token expirado")
        void shouldReturnFalseForExpiredToken() {
            var expiredJwt = new JwtUtil(SECRET, -3600000);
            var token = expiredJwt.generateToken(UUID.randomUUID(), "test@example.com");

            assertFalse(jwtUtil.isValid(token));
        }

        @Test
        @DisplayName("debe retornar false para un token con firma invalida")
        void shouldReturnFalseForWrongSignature() {
            var otherJwt = new JwtUtil("different-secret-key-that-is-also-256-bits-long-for-testing-00000", EXPIRATION);
            var token = otherJwt.generateToken(UUID.randomUUID(), "test@example.com");

            assertFalse(jwtUtil.isValid(token));
        }

        @Test
        @DisplayName("debe retornar false para null")
        void shouldReturnFalseForNull() {
            assertFalse(jwtUtil.isValid(null));
        }
    }
}
