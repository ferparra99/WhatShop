package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateProductRequest {

    @Schema(description = "ID de la categoría del producto", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "La categoría es obligatoria")
    private UUID categoryId;

    @Schema(description = "Nombre del producto", example = "Auriculares Bluetooth")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(description = "Descripción del producto", example = "Auriculares inalámbricos con cancelación de ruido")
    private String description;

    @Schema(description = "Precio del producto (mayor a 0)", example = "149990.00")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Schema(description = "Cantidad en stock", example = "25")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "URL de la imagen del producto", example = "https://ejemplo.com/producto.jpg")
    private String imageUrl;
}
