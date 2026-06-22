package com.whatshop.marketplace.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "miClave123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    private String fullName;

    @Schema(description = "Teléfono en formato Colombia (+57 + 10 dígitos)", example = "+573001234567")
    @Pattern(regexp = "^\\+57\\d{10}$", message = "El teléfono debe tener formato +57XXXXXXXXXX")
    private String phone;

    @Schema(description = "Rol del usuario", example = "BUYER", allowableValues = {"BUYER", "SELLER", "ADMIN"})
    @NotBlank(message = "El rol es obligatorio")
    private String role;
}
