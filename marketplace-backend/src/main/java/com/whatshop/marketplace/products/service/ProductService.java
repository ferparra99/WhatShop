package com.whatshop.marketplace.products.service;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.products.dto.CreateProductRequest;
import com.whatshop.marketplace.products.dto.ProductDTO;
import com.whatshop.marketplace.products.dto.ProductPageResponse;
import com.whatshop.marketplace.products.dto.UpdateProductRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    ProductPageResponse listProducts(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                     UUID sellerId, String search, Pageable pageable);
    ProductDTO getProductById(UUID id);
    ProductDTO createProduct(User user, CreateProductRequest request);
    ProductDTO updateProduct(UUID productId, User user, UpdateProductRequest request);
    void deleteProduct(UUID productId, User user);
    ProductPageResponse listMyProducts(User user, Pageable pageable);
}
