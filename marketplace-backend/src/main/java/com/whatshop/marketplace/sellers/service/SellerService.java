package com.whatshop.marketplace.sellers.service;

import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.sellers.dto.SellerProfileDTO;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;
import com.whatshop.marketplace.sellers.entity.Seller;
import com.whatshop.marketplace.sellers.repository.SellerRepository;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public List<SellerProfileDTO> listActiveSellers() {
        return sellerRepository.findByActiveTrue().stream()
                .map(this::toProfileDTO)
                .toList();
    }

    public SellerProfileDTO getSellerById(UUID id) {
        var seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        return toProfileDTO(seller);
    }

    public SellerProfileDTO getMyProfile(UUID userId) {
        var seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de vendedor no encontrado"));
        return toProfileDTO(seller);
    }

    @Transactional
    public SellerProfileDTO updateMyProfile(UUID userId, UpdateSellerRequest request) {
        var seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de vendedor no encontrado"));

        if (request.getStoreName() != null) {
            if (!request.getStoreName().equals(seller.getStoreName())
                    && sellerRepository.existsByStoreName(request.getStoreName())) {
                throw new BadRequestException("El nombre de tienda ya está en uso");
            }
            seller.setStoreName(request.getStoreName());
        }
        if (request.getDescription() != null) seller.setDescription(request.getDescription());
        if (request.getNit() != null) seller.setNit(request.getNit());
        if (request.getLogoUrl() != null) seller.setLogoUrl(request.getLogoUrl());

        sellerRepository.save(seller);
        return toProfileDTO(seller);
    }

    @Transactional
    public SellerProfileDTO createSellerProfile(UUID userId, String storeName) {
        if (sellerRepository.existsByStoreName(storeName)) {
            throw new BadRequestException("El nombre de tienda ya está en uso");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        var seller = Seller.builder()
                .user(user)
                .storeName(storeName)
                .build();

        sellerRepository.save(seller);
        return toProfileDTO(seller);
    }

    private SellerProfileDTO toProfileDTO(Seller seller) {
        return SellerProfileDTO.builder()
                .id(seller.getId())
                .userId(seller.getUser().getId())
                .storeName(seller.getStoreName())
                .description(seller.getDescription())
                .nit(seller.getNit())
                .logoUrl(seller.getLogoUrl())
                .rating(seller.getRating())
                .totalSales(seller.getTotalSales())
                .active(seller.isActive())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}
