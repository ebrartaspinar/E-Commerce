package com.ecommerce.user.application.service;

import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.user.application.dto.*;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.model.UserStatus;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.infrastructure.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .email("test@example.com")
                .password("encoded-password")
                .firstName("John")
                .lastName("Doe")
                .phone("+905551234567")
                .role(UserRole.BUYER)
                .status(UserStatus.ACTIVE)
                .build();
        testUser.setId(userId);
    }

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("should register a new user successfully and return auth tokens")
        void shouldRegisterNewUserSuccessfully() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "test@example.com", "password123", "John", "Doe", "+905551234567", UserRole.BUYER
            );

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtTokenProvider.generateAccessToken(testUser)).thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken(testUser)).thenReturn("refresh-token");
            when(jwtTokenProvider.getAccessExpiration()).thenReturn(3600000L);

            // when
            AuthResponse response = authService.register(request);

            // then
            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.expiresIn()).isEqualTo(3600000L);
            assertThat(response.tokenType()).isEqualTo("Bearer");

            verify(userRepository).existsByEmail("test@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
            verify(jwtTokenProvider).generateAccessToken(testUser);
            verify(jwtTokenProvider).generateRefreshToken(testUser);
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when email already exists")
        void shouldThrowDuplicateResourceExceptionWhenEmailExists() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "existing@example.com", "password123", "John", "Doe", null, UserRole.BUYER
            );

            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // when/then
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("existing@example.com")
                    .hasMessageContaining("already exists");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should encode password before saving user")
        void shouldEncodePasswordBeforeSaving() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "test@example.com", "rawPassword", "John", "Doe", null, UserRole.BUYER
            );

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$12$encodedHash");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("token");
            when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh");
            when(jwtTokenProvider.getAccessExpiration()).thenReturn(3600000L);

            // when
            authService.register(request);

            // then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getPassword()).isEqualTo("$2a$12$encodedHash");
            assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("should login successfully with correct credentials")
        void shouldLoginSuccessfullyWithCorrectCredentials() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "password123");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
            when(jwtTokenProvider.generateAccessToken(testUser)).thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken(testUser)).thenReturn("refresh-token");
            when(jwtTokenProvider.getAccessExpiration()).thenReturn(3600000L);

            // when
            AuthResponse response = authService.login(request);

            // then
            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.expiresIn()).isEqualTo(3600000L);

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("password123", "encoded-password");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found by email")
        void shouldThrowResourceNotFoundWhenUserNotFound() {
            // given
            LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User")
                    .hasMessageContaining("email");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("should throw BusinessRuleException when password does not match")
        void shouldThrowBusinessRuleExceptionWhenPasswordDoesNotMatch() {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

            // when/then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        }

        @Test
        @DisplayName("should throw BusinessRuleException when user account is suspended")
        void shouldThrowBusinessRuleExceptionWhenAccountSuspended() {
            // given
            testUser.setStatus(UserStatus.SUSPENDED);
            LoginRequest request = new LoginRequest("test@example.com", "password123");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

            // when/then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("suspended");

            verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        }

        @Test
        @DisplayName("should throw BusinessRuleException when user account is deactivated")
        void shouldThrowBusinessRuleExceptionWhenAccountDeactivated() {
            // given
            testUser.setStatus(UserStatus.DEACTIVATED);
            LoginRequest request = new LoginRequest("test@example.com", "password123");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

            // when/then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("account");
        }
    }

    @Nested
    @DisplayName("refreshToken()")
    class RefreshToken {

        @Test
        @DisplayName("should refresh token successfully with valid refresh token")
        void shouldRefreshTokenSuccessfully() {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken("valid-refresh-token")).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(jwtTokenProvider.generateAccessToken(testUser)).thenReturn("new-access-token");
            when(jwtTokenProvider.getAccessExpiration()).thenReturn(3600000L);

            // when
            AuthResponse response = authService.refreshToken(request);

            // then
            assertThat(response.accessToken()).isEqualTo("new-access-token");
            assertThat(response.refreshToken()).isEqualTo("valid-refresh-token");
            assertThat(response.expiresIn()).isEqualTo(3600000L);

            verify(jwtTokenProvider).validateToken("valid-refresh-token");
            verify(jwtTokenProvider).getUserIdFromToken("valid-refresh-token");
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("should throw BusinessRuleException when refresh token is invalid")
        void shouldThrowBusinessRuleExceptionWhenTokenInvalid() {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

            when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

            // when/then
            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Invalid or expired refresh token");

            verify(userRepository, never()).findById(any(UUID.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user from token is not found")
        void shouldThrowResourceNotFoundWhenUserFromTokenNotFound() {
            // given
            UUID nonExistentUserId = UUID.randomUUID();
            RefreshTokenRequest request = new RefreshTokenRequest("valid-token");

            when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken("valid-token")).thenReturn(nonExistentUserId);
            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User");
        }

        @Test
        @DisplayName("should throw BusinessRuleException when user account is not active during token refresh")
        void shouldThrowBusinessRuleExceptionWhenAccountNotActive() {
            // given
            testUser.setStatus(UserStatus.SUSPENDED);
            RefreshTokenRequest request = new RefreshTokenRequest("valid-token");

            when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken("valid-token")).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            // when/then
            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("suspended");

            verify(jwtTokenProvider, never()).generateAccessToken(any(User.class));
        }
    }
}
