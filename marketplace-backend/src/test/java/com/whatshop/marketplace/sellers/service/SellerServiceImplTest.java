package com.whatshop.marketplace.sellers.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.sellers.dto.SellerProfileDTO;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;
import com.whatshop.marketplace.sellers.entity.Seller;
import com.whatshop.marketplace.sellers.repository.SellerRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SellerServiceImpl")
class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private UserRepository userRepository;

    private SellerServiceImpl sellerService;

    private UUID sellerId;
    private UUID userId;
    private Seller seller;
    private User user;
    
    @BeforeEach
    void setUp() {
        sellerService = new SellerServiceImpl(sellerRepository, userRepository);
        sellerId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("seller@test.com")
                .fullName("Test Seller")
                .role(Role.ROLE_SELLER)
                .build();

        seller = Seller.builder()
                .id(sellerId)
                .user(user)
                .storeName("Active Store")
                .description("A description")
                .nit("123456-7")
                .logoUrl("https://example.com/logo.png")
                .rating(4.5)
                .totalSales(10)
                .active(true)
                .build();

        Seller.builder()
                .id(UUID.randomUUID())
                .user(user)
                .storeName("Inactive Store")
                .active(false)
                .build();
    }

    @Nested
    @DisplayName("listActiveSellers")
    class ListActiveSellers {

        @Test
        @DisplayName("debe retornar solo vendedores activos")
        void shouldReturnOnlyActiveSellers() {
            when(sellerRepository.findByActiveTrue()).thenReturn(List.of(seller));

            List<SellerProfileDTO> result = sellerService.listActiveSellers();

            assertEquals(1, result.size());
            assertEquals("Active Store", result.get(0).getStoreName());
        }

        @Test
        @DisplayName("debe retornar lista vacia si no hay vendedores activos")
        void shouldReturnEmptyListWhenNoActiveSellers() {
            when(sellerRepository.findByActiveTrue()).thenReturn(List.of());

            List<SellerProfileDTO> result = sellerService.listActiveSellers();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getSellerById")
    class GetSellerById {

        @Test
        @DisplayName("debe retornar vendedor por ID")
        void shouldReturnSellerById() {
            when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));

            var result = sellerService.getSellerById(sellerId);

            assertEquals("Active Store", result.getStoreName());
            assertEquals(4.5, result.getRating());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el vendedor no existe")
        void shouldThrowWhenNotFound() {
            when(sellerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> sellerService.getSellerById(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("getMyProfile")
    class GetMyProfile {

        @Test
        @DisplayName("debe retornar el perfil del vendedor autenticado")
        void shouldReturnMyProfile() {
            when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));

            var result = sellerService.getMyProfile(user);

            assertEquals("Active Store", result.getStoreName());
            assertEquals(userId, result.getUserId());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el usuario no tiene perfil de vendedor")
        void shouldThrowWhenNoSellerProfile() {
            when(sellerRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> sellerService.getMyProfile(user));
        }
    }

    @Nested
    @DisplayName("updateMyProfile")
    class UpdateMyProfile {

        @Test
        @DisplayName("debe actualizar el nombre de tienda correctamente")
        void shouldUpdateStoreName() {
            var request = new UpdateSellerRequest();
            request.setStoreName("New Store Name");
            when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
            when(sellerRepository.existsByStoreName("New Store Name")).thenReturn(false);
            when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = sellerService.updateMyProfile(user, request);

            assertEquals("New Store Name", result.getStoreName());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el nombre de tienda ya esta en uso")
        void shouldThrowWhenStoreNameAlreadyExists() {
            var request = new UpdateSellerRequest();
            request.setStoreName("Taken Name");
            when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
            when(sellerRepository.existsByStoreName("Taken Name")).thenReturn(true);

            assertThrows(BadRequestException.class, () -> sellerService.updateMyProfile(user, request));
        }

        @Test
        @DisplayName("debe permitir mantener el mismo nombre de tienda")
        void shouldAllowSameStoreName() {
            var request = new UpdateSellerRequest();
            request.setStoreName("Active Store");
            when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
            when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = sellerService.updateMyProfile(user, request);

            assertEquals("Active Store", result.getStoreName());
            verify(sellerRepository, never()).existsByStoreName(any());
        }

        @Test
        @DisplayName("debe actualizar solo los campos proporcionados")
        void shouldUpdateOnlyProvidedFields() {
            var request = new UpdateSellerRequest();
            request.setDescription("New description");
            when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
            when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = sellerService.updateMyProfile(user, request);

            assertEquals("Active Store", result.getStoreName()); // unchanged
            assertEquals("New description", result.getDescription());
            assertEquals("123456-7", result.getNit()); // unchanged
        }
    }

    @Nested
    @DisplayName("createSellerProfile")
    class CreateSellerProfile {

        @Test
        @DisplayName("debe crear perfil de vendedor exitosamente")
        void shouldCreateSellerProfile() {
            when(sellerRepository.existsByStoreName("New Store")).thenReturn(false);
            when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
                Seller s = invocation.getArgument(0);
                s.setId(UUID.randomUUID());
                return s;
            });

            var result = sellerService.createSellerProfile(user, "New Store", "Desc", "NIT123", "https://logo.com");

            assertNotNull(result);
            assertEquals("New Store", result.getStoreName());
            assertEquals("Desc", result.getDescription());
            assertEquals(userId, result.getUserId());
            assertTrue(result.isActive());
            assertEquals(0.0, result.getRating());
            assertEquals(0, result.getTotalSales());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el nombre de tienda ya existe")
        void shouldThrowWhenStoreNameExists() {
            when(sellerRepository.existsByStoreName("Duplicated")).thenReturn(true);

            assertThrows(BadRequestException.class,
                    () -> sellerService.createSellerProfile(user, "Duplicated", null, null, null));
            verify(sellerRepository, never()).save(any());
        }
    }
}
