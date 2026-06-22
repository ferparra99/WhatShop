package com.whatshop.marketplace.sellers.dto;

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
public class SellerProfileDTO {

    @Schema(description = "ID del perfil de vendedor", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID del usuario asociado")
    private UUID userId;

    @Schema(description = "Nombre de la tienda", example = "Tienda de Juan")
    private String storeName;

    @Schema(description = "Descripción de la tienda", example = "Vendemos productos artesanales colombianos")
    private String description;

    @Schema(description = "NIT de la tienda", example = "123456789-0")
    private String nit;

    @Schema(description = "URL del logo de la tienda", example = "https://ejemplo.com/logo.png")
    private String logoUrl;

    @Schema(description = "Calificación promedio", example = "4.5")
    private Double rating;

    @Schema(description = "Total de ventas", example = "150")
    private Integer totalSales;

    @Schema(description = "Si la tienda está activa", example = "true")
    private boolean active;

    @Schema(description = "Fecha de creación del perfil", example = "2026-06-22T10:30:00")
    private LocalDateTime createdAt;
}
