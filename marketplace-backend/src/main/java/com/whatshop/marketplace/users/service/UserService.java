package com.whatshop.marketplace.users.service;

import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.shared.exception.ResourceNotFoundException;
import com.whatshop.marketplace.users.dto.UpdateUserRequest;
import com.whatshop.marketplace.users.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getMyProfile(User user) {
        return toProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateMyProfile(User user, UpdateUserRequest request) {
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);
        return toProfileDTO(user);
    }

    public List<UserProfileDTO> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toProfileDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleUserEnabled(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
