package com.tuapp.marketplace.products.dto;

import com.tuapp.marketplace.products.entity.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @Schema(description = "ID del producto", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID del vendedor")
    private UUID sellerId;

    @Schema(description = "Nombre de la tienda", example = "Tienda de Juan")
    private String sellerStoreName;

    @Schema(description = "ID de la categoría")
    private UUID categoryId;

    @Schema(description = "Nombre de la categoría", example = "Electrónicos")
    private String categoryName;

    @Schema(description = "Nombre del producto", example = "Auriculares Bluetooth")
    private String name;

    @Schema(description = "Descripción del producto", example = "Auriculares inalámbricos con cancelación de ruido")
    private String description;

    @Schema(description = "Precio del producto", example = "149990.00")
    private BigDecimal price;

    @Schema(description = "Stock disponible", example = "25")
    private Integer stock;

    @Schema(description = "URL de la imagen del producto", example = "https://ejemplo.com/producto.jpg")
    private String imageUrl;

    @Schema(description = "Estado del producto")
    private ProductStatus status;

    @Schema(description = "Calificación promedio", example = "4.2")
    private Double rating;

    @Schema(description = "Fecha de creación", example = "2026-06-22T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última modificación", example = "2026-06-22T14:00:00")
    private LocalDateTime updatedAt;
}
