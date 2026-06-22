package com.tuapp.marketplace.sellers.repository;

import com.tuapp.marketplace.sellers.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Optional<Seller> findByUserId(UUID userId);
    Optional<Seller> findByStoreName(String storeName);
    boolean existsByStoreName(String storeName);
    List<Seller> findByActiveTrue();
}
