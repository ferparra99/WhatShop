package com.whatshop.marketplace.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "miClave123")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
