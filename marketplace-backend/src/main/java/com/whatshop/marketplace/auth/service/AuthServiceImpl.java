package com.whatshop.marketplace.auth.service;

import com.whatshop.marketplace.auth.dto.AuthResponse;
import com.whatshop.marketplace.auth.dto.LoginRequest;
import com.whatshop.marketplace.auth.dto.RegisterRequest;
import com.whatshop.marketplace.auth.entity.Role;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.auth.repository.UserRepository;
import com.whatshop.marketplace.sellers.service.SellerService;
import com.whatshop.marketplace.shared.exception.BadRequestException;
import com.whatshop.marketplace.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SellerService sellerService;

    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Role role;
        try {
            role = Role.valueOf("ROLE_" + request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol inválido. Use: BUYER, SELLER o ADMIN");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(role)
                .build();

        if (role == Role.ROLE_SELLER) {
            var store = request.getStore();
            if (store == null || store.getStoreName() == null || store.getStoreName().isBlank()) {
                throw new BadRequestException("El nombre de tienda es obligatorio para rol SELLER");
            }
        }

        userRepository.save(user);

        if (role == Role.ROLE_SELLER) {
            var store = request.getStore();
            sellerService.createSellerProfile(
                    user,
                    store.getStoreName(),
                    store.getDescription(),
                    store.getNit(),
                    store.getLogoUrl());
        } else if (role == Role.ROLE_ADMIN && request.getStore() != null
                && request.getStore().getStoreName() != null
                && !request.getStore().getStoreName().isBlank()) {
            sellerService.createSellerProfile(
                    user,
                    request.getStore().getStoreName(),
                    request.getStore().getDescription(),
                    request.getStore().getNit(),
                    request.getStore().getLogoUrl());
        }

        var token = jwtUtil.generateToken(user.getId(), user.getEmail());
        var authResponse = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        return ApiResponse.success(authResponse, "Usuario registrado exitosamente");
    }

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        var token = jwtUtil.generateToken(user.getId(), user.getEmail());
        var authResponse = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        return ApiResponse.success(authResponse, "Inicio de sesión exitoso");
    }

    public ApiResponse<AuthResponse> me(User user) {
        var authResponse = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        return ApiResponse.success(authResponse);
    }

    public ApiResponse<AuthResponse> refresh(User user) {       
        var token = jwtUtil.generateToken(user.getId(), user.getEmail());
        var authResponse = AuthResponse.builder()
                .token(token)
                .build();
        return ApiResponse.success(authResponse, "Token renovado exitosamente");
    }
}
