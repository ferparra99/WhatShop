package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Schema(description = "Nuevo stock", example = "50")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Nueva URL de la imagen", example = "https://ejemplo.com/nuevo-producto.jpg")
    private String imageUrl;
}
