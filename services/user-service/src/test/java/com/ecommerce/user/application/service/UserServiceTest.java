package com.ecommerce.user.application.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.user.application.dto.UpdateUserRequest;
import com.ecommerce.user.application.dto.UserResponse;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.model.UserStatus;
import com.ecommerce.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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
                .addresses(new ArrayList<>())
                .build();
        testUser.setId(userId);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUser {

        @Test
        @DisplayName("should return current user response when user exists")
        void shouldReturnCurrentUserWhenExists() {
            // given
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            // when
            UserResponse response = userService.getCurrentUser(userId);

            // then
            assertThat(response.id()).isEqualTo(userId);
            assertThat(response.email()).isEqualTo("test@example.com");
            assertThat(response.firstName()).isEqualTo("John");
            assertThat(response.lastName()).isEqualTo("Doe");
            assertThat(response.phone()).isEqualTo("+905551234567");
            assertThat(response.role()).isEqualTo(UserRole.BUYER);
            assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
            assertThat(response.addresses()).isEmpty();

            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundWhenUserDoesNotExist() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> userService.getCurrentUser(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User")
                    .hasMessageContaining("id");

            verify(userRepository).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUser {

        @Test
        @DisplayName("should update all provided fields successfully")
        void shouldUpdateAllProvidedFields() {
            // given
            UpdateUserRequest request = new UpdateUserRequest("Jane", "Smith", "+905559876543");

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            UserResponse response = userService.updateUser(userId, request);

            // then
            assertThat(response.firstName()).isEqualTo("Jane");
            assertThat(response.lastName()).isEqualTo("Smith");
            assertThat(response.phone()).isEqualTo("+905559876543");

            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should perform partial update when only firstName is provided")
        void shouldPerformPartialUpdateWithOnlyFirstName() {
            // given
            UpdateUserRequest request = new UpdateUserRequest("Jane", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            UserResponse response = userService.updateUser(userId, request);

            // then
            assertThat(response.firstName()).isEqualTo("Jane");
            assertThat(response.lastName()).isEqualTo("Doe"); // unchanged
            assertThat(response.phone()).isEqualTo("+905551234567"); // phone unchanged because request.phone() is null
        }

        @Test
        @DisplayName("should not update firstName when it is blank")
        void shouldNotUpdateFirstNameWhenBlank() {
            // given
            UpdateUserRequest request = new UpdateUserRequest("", "Smith", null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            UserResponse response = userService.updateUser(userId, request);

            // then
            assertThat(response.firstName()).isEqualTo("John"); // unchanged since blank is not considered text
            assertThat(response.lastName()).isEqualTo("Smith");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user to update does not exist")
        void shouldThrowResourceNotFoundWhenUserToUpdateDoesNotExist() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            UpdateUserRequest request = new UpdateUserRequest("Jane", null, null);

            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> userService.updateUser(nonExistentId, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should update phone to non-null value when provided")
        void shouldUpdatePhoneWhenProvided() {
            // given
            UpdateUserRequest request = new UpdateUserRequest(null, null, "+901112223344");

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            UserResponse response = userService.updateUser(userId, request);

            // then
            assertThat(response.phone()).isEqualTo("+901112223344");
            assertThat(response.firstName()).isEqualTo("John"); // unchanged
            assertThat(response.lastName()).isEqualTo("Doe"); // unchanged
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("should return user response when user exists")
        void shouldReturnUserWhenExists() {
            // given
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            // when
            UserResponse response = userService.getUserById(userId);

            // then
            assertThat(response.id()).isEqualTo(userId);
            assertThat(response.email()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundWhenNotExists() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> userService.getUserById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
