package com.tuapp.marketplace.sellers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateSellerRequest {

    @Schema(description = "Nuevo nombre de la tienda", example = "Tienda de Juan")
    private String storeName;

    @Schema(description = "Nueva descripción de la tienda", example = "Vendemos productos artesanales colombianos")
    private String description;

    @Schema(description = "Nuevo NIT", example = "123456789-0")
    private String nit;

    @Schema(description = "Nueva URL del logo", example = "https://ejemplo.com/nuevo-logo.png")
    private String logoUrl;
}
