package com.whatshop.marketplace.auth.service;

import com.whatshop.marketplace.auth.dto.AuthResponse;
import com.whatshop.marketplace.auth.dto.LoginRequest;
import com.whatshop.marketplace.auth.dto.RegisterRequest;
import com.whatshop.marketplace.auth.entity.User;
import com.whatshop.marketplace.shared.response.ApiResponse;

public interface AuthService {
    ApiResponse<AuthResponse> register(RegisterRequest request);
    ApiResponse<AuthResponse> login(LoginRequest request);
    ApiResponse<AuthResponse> me(User user);
    ApiResponse<AuthResponse> refresh(User user);
}
