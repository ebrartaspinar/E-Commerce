package com.trendyolclone.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.user.application.dto.AddressResponse;
import com.trendyolclone.user.application.dto.UpdateUserRequest;
import com.trendyolclone.user.application.dto.UserResponse;
import com.trendyolclone.user.application.service.UserService;
import com.trendyolclone.user.domain.model.UserRole;
import com.trendyolclone.user.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
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
import com.trendyolclone.user.infrastructure.config.SecurityConfig;
import com.trendyolclone.user.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserResponse createTestUserResponse() {
        return new UserResponse(
                userId,
                "test@example.com",
                "John",
                "Doe",
                "+905551234567",
                UserRole.BUYER,
                UserStatus.ACTIVE,
                Collections.emptyList(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("GET /api/v1/users/me")
    class GetCurrentUserEndpoint {

        @Test
        @DisplayName("should return 200 OK with user profile when authenticated")
        void shouldReturn200WithUserProfileWhenAuthenticated() throws Exception {
            // given
            UserResponse userResponse = createTestUserResponse();
            when(userService.getCurrentUser(userId)).thenReturn(userResponse);

            // when/then
            mockMvc.perform(get("/api/v1/users/me")
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(userId.toString()))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.firstName").value("John"))
                    .andExpect(jsonPath("$.data.lastName").value("Doe"))
                    .andExpect(jsonPath("$.data.role").value("BUYER"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return user with addresses when user has addresses")
        void shouldReturnUserWithAddresses() throws Exception {
            // given
            UUID addressId = UUID.randomUUID();
            AddressResponse addressResponse = new AddressResponse(
                    addressId, "Home", "123 Main St", "Istanbul", "Kadikoy", "34710", true
            );
            UserResponse userResponse = new UserResponse(
                    userId, "test@example.com", "John", "Doe", "+905551234567",
                    UserRole.BUYER, UserStatus.ACTIVE, List.of(addressResponse), LocalDateTime.now()
            );
            when(userService.getCurrentUser(userId)).thenReturn(userResponse);

            // when/then
            mockMvc.perform(get("/api/v1/users/me")
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.addresses").isArray())
                    .andExpect(jsonPath("$.data.addresses[0].title").value("Home"))
                    .andExpect(jsonPath("$.data.addresses[0].city").value("Istanbul"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/me")
    class UpdateCurrentUserEndpoint {

        @Test
        @DisplayName("should return 200 OK with updated user profile")
        void shouldReturn200WithUpdatedProfile() throws Exception {
            // given
            UpdateUserRequest updateRequest = new UpdateUserRequest("Jane", "Smith", "+905559876543");
            UserResponse updatedResponse = new UserResponse(
                    userId, "test@example.com", "Jane", "Smith", "+905559876543",
                    UserRole.BUYER, UserStatus.ACTIVE, Collections.emptyList(), LocalDateTime.now()
            );

            when(userService.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

            // when/then
            mockMvc.perform(put("/api/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.firstName").value("Jane"))
                    .andExpect(jsonPath("$.data.lastName").value("Smith"))
                    .andExpect(jsonPath("$.data.phone").value("+905559876543"))
                    .andExpect(jsonPath("$.message").value("User updated successfully"));
        }

        @Test
        @DisplayName("should return 200 OK with partial update")
        void shouldReturn200WithPartialUpdate() throws Exception {
            // given
            UpdateUserRequest updateRequest = new UpdateUserRequest("Jane", null, null);
            UserResponse updatedResponse = new UserResponse(
                    userId, "test@example.com", "Jane", "Doe", "+905551234567",
                    UserRole.BUYER, UserStatus.ACTIVE, Collections.emptyList(), LocalDateTime.now()
            );

            when(userService.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

            // when/then
            mockMvc.perform(put("/api/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.firstName").value("Jane"))
                    .andExpect(jsonPath("$.data.lastName").value("Doe"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class GetUserByIdEndpoint {

        @Test
        @DisplayName("should return 200 OK with user profile by ID")
        void shouldReturn200WithUserById() throws Exception {
            // given
            UUID targetUserId = UUID.randomUUID();
            UserResponse userResponse = new UserResponse(
                    targetUserId, "other@example.com", "Jane", "Smith", null,
                    UserRole.SELLER, UserStatus.ACTIVE, Collections.emptyList(), LocalDateTime.now()
            );

            when(userService.getUserById(targetUserId)).thenReturn(userResponse);

            // when/then
            mockMvc.perform(get("/api/v1/users/{id}", targetUserId)
)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(targetUserId.toString()))
                    .andExpect(jsonPath("$.data.email").value("other@example.com"));
        }
    }
}
