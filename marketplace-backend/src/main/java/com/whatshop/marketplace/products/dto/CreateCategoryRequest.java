package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @Schema(description = "Nombre de la categoría (obligatorio)", example = "Electrónica")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    @Schema(description = "URL de la imagen de la categoría (opcional)", example = "https://ejemplo.com/categoria.jpg")
    private String imageUrl;
}