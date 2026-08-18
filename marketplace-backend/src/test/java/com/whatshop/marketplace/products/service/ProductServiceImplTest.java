package com.whatshop.marketplace.products.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import com.whatshop.marketplace.products.entity.Category;
import com.whatshop.marketplace.products.entity.Product;
import com.whatshop.marketplace.products.entity.ProductStatus;
import com.whatshop.marketplace.products.repository.CategoryRepository;
import com.whatshop.marketplace.products.repository.ProductRepository;
import com.whatshop.marketplace.sellers.entity.Seller;
import com.whatshop.marketplace.sellers.repository.SellerRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SellerRepository sellerRepository;

    private ProductServiceImpl productService;

    private UUID sellerId;
    private UUID categoryId;
    private UUID productId;
    private Seller seller;
    private Category category;
    private Product activeProduct;
    private Product deletedProduct;
    private User sellerUser;
    private User adminUser;
    private User buyerUser;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, categoryRepository, sellerRepository);

        sellerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        productId = UUID.randomUUID();

        seller = Seller.builder()
                .id(sellerId)
                .storeName("Test Store")
                .active(true)
                .build();

        category = Category.builder()
                .id(categoryId)
                .name("Test Category")
                .slug("test-category")
                .build();

        activeProduct = Product.builder()
                .id(productId)
                .seller(seller)
                .category(category)
                .name("Test Product")
                .description("A test product")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .status(ProductStatus.ACTIVE)
                .build();

        deletedProduct = Product.builder()
                .id(UUID.randomUUID())
                .seller(seller)
                .category(category)
                .name("Deleted Product")
                .price(new BigDecimal("50.00"))
                .stock(0)
                .status(ProductStatus.DELETED)
                .build();

        sellerUser = User.builder()
                .id(UUID.randomUUID())
                .email("seller@test.com")
                .role(Role.ROLE_SELLER)
                .build();

        adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        buyerUser = User.builder()
                .id(UUID.randomUUID())
                .email("buyer@test.com")
                .role(Role.ROLE_BUYER)
                .build();
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        private CreateProductRequest validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new CreateProductRequest();
            validRequest.setCategoryId(categoryId);
            validRequest.setName("New Product");
            validRequest.setDescription("Description");
            validRequest.setPrice(new BigDecimal("29.99"));
            validRequest.setStock(5);
            validRequest.setImageUrl("https://example.com/img.jpg");
        }

        @Test
        @DisplayName("debe crear un producto exitosamente con seller existente")
        void shouldCreateProductWithExistingSeller() {
            when(sellerRepository.findByUserId(sellerUser.getId())).thenReturn(Optional.of(seller));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = productService.createProduct(sellerUser, validRequest);

            assertNotNull(result);
            assertEquals("New Product", result.getName());
            assertEquals(sellerId, result.getSellerId());
            assertEquals(categoryId, result.getCategoryId());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("debe lanzar excepcion si el usuario no tiene perfil de vendedor")
        void shouldThrowWhenUserHasNoSellerProfile() {
            when(sellerRepository.findByUserId(buyerUser.getId())).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class,
                    () -> productService.createProduct(buyerUser, validRequest));
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepcion si la categoria no existe")
        void shouldThrowWhenCategoryNotFound() {
            when(sellerRepository.findByUserId(sellerUser.getId())).thenReturn(Optional.of(seller));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.createProduct(sellerUser, validRequest));
        }

        @Test
        @DisplayName("debe crear producto con ADMIN usando perfil de vendedor existente")
        void shouldCreateWithAdminAndExistingSeller() {
            when(sellerRepository.findByUserId(adminUser.getId())).thenReturn(Optional.of(seller));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = productService.createProduct(adminUser, validRequest);

            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("debe lanzar excepcion si ADMIN no tiene seller y no hay fallback")
        void shouldThrowWhenAdminHasNoSeller() {
            when(sellerRepository.findByUserId(adminUser.getId())).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class,
                    () -> productService.createProduct(adminUser, validRequest));
        }
    }

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("debe retornar producto activo")
        void shouldReturnActiveProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

            var result = productService.getProductById(productId);

            assertNotNull(result);
            assertEquals(activeProduct.getName(), result.getName());
        }

        @Test
        @DisplayName("debe lanzar 404 si el producto no existe")
        void shouldThrow404WhenNotFound() {
            when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getProductById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("debe lanzar 404 si el producto esta eliminado")
        void shouldThrow404WhenProductIsDeleted() {
            when(productRepository.findById(deletedProduct.getId())).thenReturn(Optional.of(deletedProduct));

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getProductById(deletedProduct.getId()));
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        private UpdateProductRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UpdateProductRequest();
            updateRequest.setName("Updated Name");
            updateRequest.setPrice(new BigDecimal("49.99"));
        }

        @Test
        @DisplayName("debe actualizar producto propio del seller")
        void shouldUpdateOwnProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
            when(sellerRepository.findByUserId(sellerUser.getId())).thenReturn(Optional.of(seller));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = productService.updateProduct(productId, sellerUser, updateRequest);

            assertEquals("Updated Name", result.getName());
            assertEquals(new BigDecimal("49.99"), result.getPrice());
        }

        @Test
        @DisplayName("debe permitir a ADMIN actualizar cualquier producto")
        void shouldAllowAdminToUpdateAnyProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = productService.updateProduct(productId, adminUser, updateRequest);

            assertEquals("Updated Name", result.getName());
        }

        @Test
        @DisplayName("debe lanzar excepcion si seller intenta actualizar producto ajeno")
        void shouldThrowWhenSellerUpdatesOthersProduct() {
            var otherSeller = Seller.builder().id(UUID.randomUUID()).storeName("Other").build();
            var otherUser = User.builder().id(UUID.randomUUID()).email("other@test.com")
                    .role(Role.ROLE_SELLER).build();
            var otherProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .seller(otherSeller)
                    .category(category)
                    .name("Other's Product")
                    .price(new BigDecimal("10.00"))
                    .stock(1)
                    .status(ProductStatus.ACTIVE)
                    .build();

            when(productRepository.findById(otherProduct.getId())).thenReturn(Optional.of(otherProduct));
            when(sellerRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(seller));

            assertThrows(BadRequestException.class,
                    () -> productService.updateProduct(otherProduct.getId(), sellerUser, updateRequest));
        }

        @Test
        @DisplayName("debe lanzar excepcion si el producto no existe")
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateProduct(UUID.randomUUID(), sellerUser, updateRequest));
        }
    }

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("debe realizar soft delete del producto propio")
        void shouldSoftDeleteOwnProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
            when(sellerRepository.findByUserId(sellerUser.getId())).thenReturn(Optional.of(seller));

            productService.deleteProduct(productId, sellerUser);

            assertEquals(ProductStatus.DELETED, activeProduct.getStatus());
            verify(productRepository).save(activeProduct);
        }

        @Test
        @DisplayName("debe permitir a ADMIN eliminar cualquier producto")
        void shouldAllowAdminToDeleteAnyProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

            productService.deleteProduct(productId, adminUser);

            assertEquals(ProductStatus.DELETED, activeProduct.getStatus());
            verify(productRepository).save(activeProduct);
        }

        @Test
        @DisplayName("debe lanzar excepcion si el producto no existe")
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.deleteProduct(UUID.randomUUID(), sellerUser));
        }

        @Test
        @DisplayName("debe lanzar excepcion si seller intenta eliminar producto ajeno")
        void shouldThrowWhenSellerDeletesOthersProduct() {
            var otherSeller = Seller.builder().id(UUID.randomUUID()).storeName("Other").build();
            var otherProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .seller(otherSeller)
                    .name("Other's Product")
                    .price(new BigDecimal("10.00"))
                    .stock(1)
                    .status(ProductStatus.ACTIVE)
                    .build();

            when(productRepository.findById(otherProduct.getId())).thenReturn(Optional.of(otherProduct));
            when(sellerRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(seller));

            assertThrows(BadRequestException.class,
                    () -> productService.deleteProduct(otherProduct.getId(), sellerUser));
        }
    }

    @Nested
    @DisplayName("listProducts")
    class ListProducts {

        @Test
        @DisplayName("debe retornar productos paginados con filtros")
        void shouldReturnPaginatedProducts() {
            var pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(List.of(activeProduct));
            when(productRepository.searchProducts(any(), any(), any(), any(), anyString(), any()))
                    .thenReturn(page);

            var result = productService.listProducts(categoryId, BigDecimal.ZERO,
                    new BigDecimal("1000"), sellerId, "test", pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(0, result.getPage());
        }
    }

    @Nested
    @DisplayName("listMyProducts")
    class ListMyProducts {

        @Test
        @DisplayName("debe retornar los productos del seller autenticado")
        void shouldReturnSellersProducts() {
            var pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(List.of(activeProduct));
            when(sellerRepository.findByUserId(sellerUser.getId())).thenReturn(Optional.of(seller));
            when(productRepository.findBySellerIdAndStatus(seller.getId(), ProductStatus.ACTIVE, pageable))
                    .thenReturn(page);

            var result = productService.listMyProducts(sellerUser, pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("Test Product", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("debe lanzar excepcion si el usuario no tiene perfil de vendedor")
        void shouldThrowWhenNoSellerProfile() {
            when(sellerRepository.findByUserId(buyerUser.getId())).thenReturn(Optional.empty());

            assertThrows(BadRequestException.class,
                    () -> productService.listMyProducts(buyerUser, PageRequest.of(0, 20)));
        }
    }
}
