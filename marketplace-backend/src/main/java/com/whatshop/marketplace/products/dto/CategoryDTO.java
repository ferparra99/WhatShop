package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    @Schema(description = "ID de la categoría", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nombre de la categoría", example = "Electrónica")
    private String name;

    @Schema(description = "Slug para URL amigable", example = "electronica")
    private String slug;

    @Schema(description = "URL de la imagen de la categoría")
    private String imageUrl;
}