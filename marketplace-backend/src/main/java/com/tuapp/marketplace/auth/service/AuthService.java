package com.tuapp.marketplace.auth.service;

import com.tuapp.marketplace.auth.dto.AuthResponse;
import com.tuapp.marketplace.auth.dto.LoginRequest;
import com.tuapp.marketplace.auth.dto.RegisterRequest;
import com.tuapp.marketplace.auth.entity.Role;
import com.tuapp.marketplace.auth.entity.User;
import com.tuapp.marketplace.auth.repository.UserRepository;
import com.tuapp.marketplace.shared.exception.BadRequestException;
import com.tuapp.marketplace.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

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

        userRepository.save(user);

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

    public ApiResponse<String> refresh(User user) {
        var token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return ApiResponse.success(token, "Token renovado exitosamente");
    }
}
