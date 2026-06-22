package com.tuapp.marketplace.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Schema(description = "Nuevo nombre completo", example = "Juan Carlos Pérez")
    private String fullName;

    @Schema(description = "Nuevo teléfono formato Colombia", example = "+573001234567")
    @Pattern(regexp = "^\\+57\\d{10}$", message = "El teléfono debe tener formato +57XXXXXXXXXX")
    private String phone;
}
