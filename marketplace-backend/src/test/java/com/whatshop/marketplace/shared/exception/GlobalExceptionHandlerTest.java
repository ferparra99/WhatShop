package com.whatshop.marketplace.shared.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("ResourceNotFoundException")
    class ResourceNotFound {

        @Test
        @DisplayName("debe retornar 404 con mensaje")
        void shouldReturn404() {
            var ex = new ResourceNotFoundException("Usuario no encontrado");

            var result = handler.handleNotFound(ex);

            assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatus());
            assertEquals("Usuario no encontrado", result.getDetail());
        }
    }

    @Nested
    @DisplayName("BadRequestException")
    class BadRequest {

        @Test
        @DisplayName("debe retornar 400 con mensaje")
        void shouldReturn400() {
            var ex = new BadRequestException("Datos invalidos");

            var result = handler.handleBadRequest(ex);

            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
            assertEquals("Datos invalidos", result.getDetail());
        }
    }

    @Nested
    @DisplayName("UnauthorizedException")
    class Unauthorized {

        @Test
        @DisplayName("debe retornar 401 con mensaje")
        void shouldReturn401() {
            var ex = new UnauthorizedException("No autorizado");

            var result = handler.handleUnauthorized(ex);

            assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getStatus());
            assertEquals("No autorizado", result.getDetail());
        }
    }

    @Nested
    @DisplayName("BadCredentialsException")
    class BadCredentials {

        @Test
        @DisplayName("debe retornar 401 con mensaje generico")
        void shouldReturn401WithGenericMessage() {
            var ex = new BadCredentialsException("Bad credentials");

            var result = handler.handleBadCredentials(ex);

            assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getStatus());
            assertEquals("Credenciales inv\u00e1lidas", result.getDetail());
        }
    }

    @Nested
    @DisplayName("AccessDeniedException")
    class AccessDenied {

        @Test
        @DisplayName("debe retornar 403")
        void shouldReturn403() {
            var ex = new AccessDeniedException("Acceso denegado");

            var result = handler.handleAccessDenied(ex);

            assertEquals(HttpStatus.FORBIDDEN.value(), result.getStatus());
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException")
    class ValidationError {

        @Test
        @DisplayName("debe retornar 400 con errores de validacion")
        void shouldReturn400WithValidationErrors() {
            var ex = mock(MethodArgumentNotValidException.class);
            var bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object");
            bindingResult.addError(new FieldError("object", "email", "El email es obligatorio"));
            when(ex.getBindingResult()).thenReturn(bindingResult);

            var result = handler.handleValidationErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
            assertTrue(result.getDetail().contains("email"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException")
    class TypeMismatch {

        @Test
        @DisplayName("debe retornar 400")
        void shouldReturn400() {
            var ex = new MethodArgumentTypeMismatchException("value", null, null, null, null);

            var result = handler.handleTypeMismatch(ex);

            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        }
    }

    @Nested
    @DisplayName("MissingServletRequestParameterException")
    class MissingParam {

        @Test
        @DisplayName("debe retornar 400")
        void shouldReturn400() {
            var ex = new MissingServletRequestParameterException("param", "String");

            var result = handler.handleMissingParam(ex);

            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
            assertTrue(result.getDetail().contains("param"));
        }
    }

    @Nested
    @DisplayName("Exception generica")
    class GenericException {

        @Test
        @DisplayName("debe retornar 500")
        void shouldReturn500() {
            var ex = new RuntimeException("Error interno");

            var result = handler.handleGeneral(ex);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
            assertEquals("Error interno del servidor", result.getDetail());
        }
    }
}
