package com.whatshop.marketplace.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class RegisterRequest {

    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "miClave123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    @ToString.Exclude
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

    @Schema(description = "Datos de la tienda (obligatorio si rol=SELLER)")
    @Valid
    private StoreInfo store;

    @AssertTrue(message = "El nombre de tienda es obligatorio para rol SELLER y ADMIN")
    private boolean isStoreValid() {
        if ("SELLER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return store != null && store.getStoreName() != null && !store.getStoreName().isBlank();
        }
        return true;
    }

    @Data
    public static class StoreInfo {
        @Schema(description = "Nombre de la tienda", example = "Tienda de María")
        @NotBlank(message = "El nombre de tienda es obligatorio")
        private String storeName;

        @Schema(description = "Descripción de la tienda")
        private String description;

        @Schema(description = "NIT del vendedor")
        private String nit;

        @Schema(description = "URL del logo")
        private String logoUrl;
    }
}
