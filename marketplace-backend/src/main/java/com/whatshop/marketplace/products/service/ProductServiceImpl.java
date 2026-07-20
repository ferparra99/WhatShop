package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.ProductDTO;
import com.whatshop.marketplace.products.dto.ProductPageResponse;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import com.whatshop.marketplace.products.entity.Product;
import com.whatshop.marketplace.products.entity.ProductStatus;
import com.whatshop.marketplace.products.repository.CategoryRepository;
import com.whatshop.marketplace.products.repository.ProductRepository;
import com.whatshop.marketplace.sellers.entity.Seller;
import com.whatshop.marketplace.sellers.repository.SellerRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    public ProductPageResponse listProducts(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                            UUID sellerId, String search, Pageable pageable) {
        String pattern = "%" + (search != null ? search : "") + "%";
        var page = productRepository.searchProducts(categoryId, minPrice, maxPrice, sellerId, pattern, pageable);
        return toPageResponse(page);
    }

    public ProductDTO getProductById(UUID id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        return toDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(User user, CreateProductRequest request) {
        var seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Debes tener un perfil de vendedor para crear productos"));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        var product = Product.builder()
                .seller(seller)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .build();

        productRepository.save(product);
        return toDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(UUID productId, User user, UpdateProductRequest request) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        validateSellerOwnership(product, user);

        if (request.getCategoryId() != null) {
            var category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());

        productRepository.save(product);
        return toDTO(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, User user) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        validateSellerOwnership(product, user);
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    public ProductPageResponse listMyProducts(User user, Pageable pageable) {
        var seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No tienes un perfil de vendedor"));

        var page = productRepository.findBySellerIdAndStatus(seller.getId(), ProductStatus.ACTIVE, pageable);
        return toPageResponse(page);
    }

    private void validateSellerOwnership(Product product, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) return;

        var seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No tienes un perfil de vendedor"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("No puedes modificar un producto que no te pertenece");
        }
    }

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sellerId(product.getSeller().getId())
                .sellerStoreName(product.getSeller().getStoreName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .rating(product.getRating())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductPageResponse toPageResponse(Page<Product> page) {
        return ProductPageResponse.builder()
                .content(page.getContent().stream().map(this::toDTO).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
