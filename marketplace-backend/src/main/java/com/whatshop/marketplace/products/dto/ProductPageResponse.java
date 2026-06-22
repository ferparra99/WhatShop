package com.whatshop.marketplace.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse {

    @Schema(description = "Lista de productos de la página actual")
    private List<ProductDTO> content;

    @Schema(description = "Número de página actual (0-based)", example = "0")
    private int page;

    @Schema(description = "Cantidad de elementos por página", example = "20")
    private int size;

    @Schema(description = "Total de elementos en toda la búsqueda", example = "150")
    private long totalElements;

    @Schema(description = "Total de páginas", example = "8")
    private int totalPages;

    @Schema(description = "Indica si es la última página", example = "false")
    private boolean last;
}
