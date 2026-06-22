package com.whatshop.marketplace.users.dto;

import com.whatshop.marketplace.auth.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    @Schema(description = "ID único del usuario", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String email;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Teléfono", example = "+573001234567")
    private String phone;

    @Schema(description = "Rol del usuario")
    private Role role;

    @Schema(description = "Si la cuenta está activa", example = "true")
    private boolean enabled;

    @Schema(description = "Fecha de creación", example = "2026-06-22T10:30:00")
    private LocalDateTime createdAt;
}
