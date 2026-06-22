package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateProductRequest {

    @Schema(description = "Nueva categoría del producto")
    private UUID categoryId;

    @Schema(description = "Nuevo nombre del producto", example = "Auriculares Bluetooth Pro")
    private String name;

    @Schema(description = "Nueva descripción del producto", example = "Versión mejorada con mayor alcance")
    private String description;

    @Schema(description = "Nuevo precio", example = "199990.00")
    private BigDecimal price;

    @Schema(description = "Nuevo stock", example = "50")
    private Integer stock;

    @Schema(description = "Nueva URL de la imagen", example = "https://ejemplo.com/nuevo-producto.jpg")
    private String imageUrl;
}
