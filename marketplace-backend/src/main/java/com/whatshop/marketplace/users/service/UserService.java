package com.whatshop.marketplace.users.service;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.users.dto.UpdateUserRequest;
import com.whatshop.marketplace.users.dto.UserProfileDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserProfileDTO getMyProfile(User user);
    UserProfileDTO updateMyProfile(User user, UpdateUserRequest request);
    List<UserProfileDTO> listAllUsers();
    void toggleUserEnabled(UUID userId);
}
