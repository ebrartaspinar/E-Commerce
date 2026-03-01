package com.trendyolclone.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.exception.DuplicateResourceException;
import com.trendyolclone.common.exception.GlobalExceptionHandler;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.user.application.dto.AuthResponse;
import com.trendyolclone.user.application.dto.LoginRequest;
import com.trendyolclone.user.application.dto.RefreshTokenRequest;
import com.trendyolclone.user.application.dto.RegisterRequest;
import com.trendyolclone.user.application.service.AuthService;
import com.trendyolclone.user.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import com.trendyolclone.user.infrastructure.config.SecurityConfig;
import com.trendyolclone.user.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class RegisterEndpoint {

        @Test
        @DisplayName("should return 201 CREATED with auth tokens on successful registration")
        void shouldReturn201OnSuccessfulRegistration() throws Exception {
            // given
            RegisterRequest request = new RegisterRequest(
                    "test@example.com", "password123", "John", "Doe", "+905551234567", UserRole.BUYER
            );
            AuthResponse authResponse = new AuthResponse("access-token", "refresh-token", 3600000L);

            when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn").value(3600000))
                    .andExpect(jsonPath("$.message").value("User registered successfully"));
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when email is missing")
        void shouldReturn400WhenEmailMissing() throws Exception {
            // given
            String requestJson = """
                    {
                        "password": "password123",
                        "firstName": "John",
                        "lastName": "Doe",
                        "role": "BUYER"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when email format is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            // given
            String requestJson = """
                    {
                        "email": "not-an-email",
                        "password": "password123",
                        "firstName": "John",
                        "lastName": "Doe",
                        "role": "BUYER"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when password is too short")
        void shouldReturn400WhenPasswordTooShort() throws Exception {
            // given
            String requestJson = """
                    {
                        "email": "test@example.com",
                        "password": "short",
                        "firstName": "John",
                        "lastName": "Doe",
                        "role": "BUYER"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when firstName is blank")
        void shouldReturn400WhenFirstNameBlank() throws Exception {
            // given
            String requestJson = """
                    {
                        "email": "test@example.com",
                        "password": "password123",
                        "firstName": "",
                        "lastName": "Doe",
                        "role": "BUYER"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when role is missing")
        void shouldReturn400WhenRoleMissing() throws Exception {
            // given
            String requestJson = """
                    {
                        "email": "test@example.com",
                        "password": "password123",
                        "firstName": "John",
                        "lastName": "Doe"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should handle DuplicateResourceException from service")
        void shouldHandleDuplicateResourceException() throws Exception {
            // given
            RegisterRequest request = new RegisterRequest(
                    "existing@example.com", "password123", "John", "Doe", null, UserRole.BUYER
            );

            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new DuplicateResourceException("User with email 'existing@example.com' already exists"));

            // when/then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("should return 200 OK with auth tokens on successful login")
        void shouldReturn200OnSuccessfulLogin() throws Exception {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "password123");
            AuthResponse authResponse = new AuthResponse("access-token", "refresh-token", 3600000L);

            when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

            // when/then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                    .andExpect(jsonPath("$.message").value("Login successful"));
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when email is missing in login")
        void shouldReturn400WhenEmailMissingInLogin() throws Exception {
            // given
            String requestJson = """
                    {
                        "password": "password123"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when password is missing in login")
        void shouldReturn400WhenPasswordMissingInLogin() throws Exception {
            // given
            String requestJson = """
                    {
                        "email": "test@example.com"
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should handle ResourceNotFoundException for wrong email")
        void shouldHandleResourceNotFoundForWrongEmail() throws Exception {
            // given
            LoginRequest request = new LoginRequest("wrong@example.com", "password123");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new ResourceNotFoundException("User", "email", "wrong@example.com"));

            // when/then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should handle BusinessRuleException for wrong password")
        void shouldHandleBusinessRuleExceptionForWrongPassword() throws Exception {
            // given
            LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new BusinessRuleException("Invalid credentials"));

            // when/then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh")
    class RefreshEndpoint {

        @Test
        @DisplayName("should return 200 OK with new access token on successful refresh")
        void shouldReturn200OnSuccessfulRefresh() throws Exception {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
            AuthResponse authResponse = new AuthResponse("new-access-token", "valid-refresh-token", 3600000L);

            when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

            // when/then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("valid-refresh-token"))
                    .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST when refresh token is blank")
        void shouldReturn400WhenRefreshTokenBlank() throws Exception {
            // given
            String requestJson = """
                    {
                        "refreshToken": ""
                    }
                    """;

            // when/then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should handle BusinessRuleException for invalid refresh token")
        void shouldHandleBusinessRuleExceptionForInvalidRefreshToken() throws Exception {
            // given
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

            when(authService.refreshToken(any(RefreshTokenRequest.class)))
                    .thenThrow(new BusinessRuleException("Invalid or expired refresh token"));

            // when/then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}
