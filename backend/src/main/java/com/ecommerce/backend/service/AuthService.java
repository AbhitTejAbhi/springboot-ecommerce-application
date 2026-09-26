package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.RegisterRequest;
import com.ecommerce.backend.dto.response.AuthResponse;

/**
 * Authentication contract: new user registration and existing-user
 * login, both resulting in an issued JWT plus the minimal user
 * identity needed by the client.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}