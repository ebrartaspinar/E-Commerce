package com.stardrop.service;

import com.stardrop.config.JwtUtil;
import com.stardrop.dto.AuthResponse;
import com.stardrop.dto.LoginRequest;
import com.stardrop.dto.RegisterRequest;
import com.stardrop.model.User;
import com.stardrop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@stardrop.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.BUYER)
                .build();
    }

    @Test
    @DisplayName("register - should create user and return token")
    void register_success() {
        RegisterRequest request = new RegisterRequest("new@stardrop.com", "pass123", "New", "User");

        when(userRepository.existsByEmail("new@stardrop.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken("new@stardrop.com", "BUYER")).thenReturn("jwt_token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.email()).isEqualTo("new@stardrop.com");
        assertThat(response.firstName()).isEqualTo("New");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - should throw if email exists")
    void register_duplicateEmail() {
        RegisterRequest request = new RegisterRequest("test@stardrop.com", "pass", "A", "B");

        when(userRepository.existsByEmail("test@stardrop.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login - should return token for valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest("test@stardrop.com", "password");

        when(userRepository.findByEmail("test@stardrop.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("test@stardrop.com", "BUYER")).thenReturn("jwt_token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.email()).isEqualTo("test@stardrop.com");
    }

    @Test
    @DisplayName("login - should throw for wrong password")
    void login_wrongPassword() {
        LoginRequest request = new LoginRequest("test@stardrop.com", "wrong");

        when(userRepository.findByEmail("test@stardrop.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid");
    }

    @Test
    @DisplayName("login - should throw for non-existent email")
    void login_emailNotFound() {
        LoginRequest request = new LoginRequest("nobody@stardrop.com", "pass");

        when(userRepository.findByEmail("nobody@stardrop.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class);
    }
}
