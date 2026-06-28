package com.whatshop.marketplace.sellers.service;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.sellers.dto.SellerProfileDTO;
import com.whatshop.marketplace.sellers.dto.UpdateSellerRequest;

import java.util.List;
import java.util.UUID;

public interface SellerService {
    List<SellerProfileDTO> listActiveSellers();
    SellerProfileDTO getSellerById(UUID id);
    SellerProfileDTO getMyProfile(User user);
    SellerProfileDTO updateMyProfile(User user, UpdateSellerRequest request);
    SellerProfileDTO createSellerProfile(User user, String storeName,
                                          String description, String nit, String logoUrl);
}
