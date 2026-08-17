package com.whatshop.marketplace.sellers.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.service.ProductService;
import com.whatshop.marketplace.sellers.dto.SellerProfileDTO;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;
import com.whatshop.marketplace.sellers.service.SellerService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("SellerController")
class SellerControllerTest {

    @Mock
    private SellerService sellerService;
    @Mock
    private ProductService productService;

    private SellerController sellerController;

    private User sellerUser;
    private SellerProfileDTO sellerDto;

    @BeforeEach
    void setUp() {
        sellerController = new SellerController(sellerService, productService);
        sellerUser = User.builder()
                .id(UUID.randomUUID())
                .email("seller@test.com")
                .role(Role.ROLE_SELLER)
                .build();
        sellerDto = SellerProfileDTO.builder()
                .id(UUID.randomUUID())
                .userId(sellerUser.getId())
                .storeName("Test Store")
                .active(true)
                .rating(4.5)
                .totalSales(10)
                .build();
    }

    @Test
    @DisplayName("GET /sellers debe retornar lista de vendedores activos")
    void listActiveSellersShouldReturnAllActiveSellers() {
        when(sellerService.listActiveSellers()).thenReturn(List.of(sellerDto));

        var response = sellerController.listActiveSellers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody().getData()).size());
    }

    @Test
    @DisplayName("GET /sellers/{id} debe retornar perfil publico del vendedor")
    void getSellerByIdShouldReturnProfile() {
        var sellerId = UUID.randomUUID();
        when(sellerService.getSellerById(sellerId)).thenReturn(sellerDto);

        var response = sellerController.getSellerById(sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Store", ((com.whatshop.marketplace.sellers.dto.SellerProfileDTO) response.getBody().getData()).getStoreName());
    }

    @Test
    @DisplayName("GET /sellers/me debe retornar perfil del vendedor autenticado")
    void getMyProfileShouldReturnProfile() {
        when(sellerService.getMyProfile(any(User.class))).thenReturn(sellerDto);

        var response = sellerController.getMyProfile(sellerUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellerUser.getId(), ((com.whatshop.marketplace.sellers.dto.SellerProfileDTO) response.getBody().getData()).getUserId());
    }

    @Test
    @DisplayName("PUT /sellers/me debe actualizar perfil")
    void updateMyProfileShouldReturnUpdatedProfile() {
        var request = new UpdateSellerRequest();
        request.setStoreName("Updated Store");
        var updatedDto = SellerProfileDTO.builder()
                .id(sellerDto.getId())
                .userId(sellerUser.getId())
                .storeName("Updated Store")
                .active(true)
                .build();
        when(sellerService.updateMyProfile(any(User.class), any(UpdateSellerRequest.class)))
                .thenReturn(updatedDto);

        var response = sellerController.updateMyProfile(sellerUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Store", ((com.whatshop.marketplace.sellers.dto.SellerProfileDTO) response.getBody().getData()).getStoreName());
    }

    @Test
    @DisplayName("GET /sellers/me/products debe retornar productos paginados")
    void listMyProductsShouldReturnPage() {
        var pageable = PageRequest.of(0, 20);
        var pageResponse = com.whatshop.marketplace.products.dto.ProductPageResponse.builder()
                .content(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .build();
        when(productService.listMyProducts(any(User.class), any(Pageable.class)))
                .thenReturn(pageResponse);

        var response = sellerController.listMyProducts(sellerUser, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
