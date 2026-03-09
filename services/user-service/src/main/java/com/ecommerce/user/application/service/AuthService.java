package com.ecommerce.user.application.service;

import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.user.application.dto.*;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserStatus;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.infrastructure.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User with email '" + request.email() + "' already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);

        return new AuthResponse(accessToken, refreshToken, jwtTokenProvider.getAccessExpiration());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessRuleException("Invalid credentials");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleException("User account is " + user.getStatus().name().toLowerCase());
        }

        log.info("User logged in successfully: userId={}, email={}", user.getId(), user.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, jwtTokenProvider.getAccessExpiration());
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessRuleException("Invalid or expired refresh token");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessRuleException("User account is " + user.getStatus().name().toLowerCase());
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return new AuthResponse(newAccessToken, refreshToken, jwtTokenProvider.getAccessExpiration());
    }
}
