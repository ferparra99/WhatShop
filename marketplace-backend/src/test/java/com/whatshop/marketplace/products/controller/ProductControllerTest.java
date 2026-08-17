package com.whatshop.marketplace.products.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.ProductDTO;
import com.whatshop.marketplace.products.dto.ProductPageResponse;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import com.whatshop.marketplace.products.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    private User sellerUser;
    private ProductDTO productDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
        productId = UUID.randomUUID();
        sellerUser = User.builder()
                .id(UUID.randomUUID())
                .email("seller@test.com")
                .role(Role.ROLE_SELLER)
                .build();
        productDto = ProductDTO.builder()
                .id(productId)
                .sellerId(UUID.randomUUID())
                .sellerStoreName("Test Store")
                .categoryId(UUID.randomUUID())
                .categoryName("Category")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .build();
    }

    @Test
    @DisplayName("GET /products debe retornar productos paginados")
    void listProductsShouldReturnPage() {
        var pageable = PageRequest.of(0, 20);
        var pageResponse = ProductPageResponse.builder()
                .content(List.of(productDto))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();
        when(productService.listProducts(any(), any(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        var response = productController.listProducts(null, null, null, null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((ProductPageResponse) response.getBody().getData()).getContent().size());
    }

    @Test
    @DisplayName("GET /products/{id} debe retornar detalle del producto")
    void getProductByIdShouldReturnProduct() {
        when(productService.getProductById(productId)).thenReturn(productDto);

        var response = productController.getProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product", ((ProductDTO) response.getBody().getData()).getName());
    }

    @Test
    @DisplayName("GET /products/search debe buscar productos")
    void searchProductsShouldReturnResults() {
        var pageable = PageRequest.of(0, 20);
        var pageResponse = ProductPageResponse.builder()
                .content(List.of(productDto))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();
        when(productService.listProducts(any(), any(), any(), any(), eq("test"), any()))
                .thenReturn(pageResponse);

        var response = productController.searchProducts("test", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /products debe crear producto y retornar 201")
    void createProductShouldReturn201() {
        var request = new CreateProductRequest();
        request.setCategoryId(UUID.randomUUID());
        request.setName("New Product");
        request.setPrice(new BigDecimal("29.99"));
        request.setStock(5);
        when(productService.createProduct(any(User.class), any(CreateProductRequest.class)))
                .thenReturn(productDto);

        var response = productController.createProduct(sellerUser, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product", ((ProductDTO) response.getBody().getData()).getName());
    }

    @Test
    @DisplayName("PUT /products/{id} debe actualizar producto")
    void updateProductShouldReturnUpdatedProduct() {
        var request = new UpdateProductRequest();
        request.setName("Updated");
        var updatedDto = ProductDTO.builder()
                .id(productId)
                .name("Updated")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .build();
        when(productService.updateProduct(any(UUID.class), any(User.class), any(UpdateProductRequest.class)))
                .thenReturn(updatedDto);

        var response = productController.updateProduct(productId, sellerUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", ((ProductDTO) response.getBody().getData()).getName());
    }

    @Test
    @DisplayName("DELETE /products/{id} debe eliminar producto")
    void deleteProductShouldSucceed() {
        doNothing().when(productService).deleteProduct(any(UUID.class), any(User.class));

        var response = productController.deleteProduct(productId, sellerUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).deleteProduct(productId, sellerUser);
    }
}
