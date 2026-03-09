package com.ecommerce.user.infrastructure.config;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private UUID userId;

    // A 256-bit secret key (32+ bytes) required for HS256
    private static final String TEST_SECRET = "ThisIsATestSecretKeyThatIsAtLeast32BytesLong!!";
    private static final long ACCESS_EXPIRATION = 3600000L; // 1 hour
    private static final long REFRESH_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessExpiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", REFRESH_EXPIRATION);

        userId = UUID.randomUUID();
        testUser = User.builder()
                .email("test@example.com")
                .password("encoded-password")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.BUYER)
                .status(UserStatus.ACTIVE)
                .build();
        testUser.setId(userId);
    }

    @Nested
    @DisplayName("generateAccessToken()")
    class GenerateAccessToken {

        @Test
        @DisplayName("should generate a non-null, non-empty JWT access token")
        void shouldGenerateNonNullAccessToken() {
            // when
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // then
            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("should generate a valid JWT with three parts separated by dots")
        void shouldGenerateValidJwtStructure() {
            // when
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // then
            String[] parts = token.split("\\.");
            assertThat(parts).hasSize(3); // header.payload.signature
        }

        @Test
        @DisplayName("should embed user ID as subject in the access token")
        void shouldEmbedUserIdAsSubject() {
            // when
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // then
            UUID extractedUserId = jwtTokenProvider.getUserIdFromToken(token);
            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("should embed user role as claim in the access token")
        void shouldEmbedUserRoleAsClaim() {
            // when
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // then
            String role = jwtTokenProvider.getRoleFromToken(token);
            assertThat(role).isEqualTo("BUYER");
        }

        @Test
        @DisplayName("should embed different roles correctly")
        void shouldEmbedDifferentRolesCorrectly() {
            // given
            testUser.setRole(UserRole.ADMIN);

            // when
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // then
            String role = jwtTokenProvider.getRoleFromToken(token);
            assertThat(role).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("generateRefreshToken()")
    class GenerateRefreshToken {

        @Test
        @DisplayName("should generate a non-null, non-empty JWT refresh token")
        void shouldGenerateNonNullRefreshToken() {
            // when
            String token = jwtTokenProvider.generateRefreshToken(testUser);

            // then
            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("should generate a different token than the access token")
        void shouldGenerateDifferentTokenThanAccessToken() {
            // when
            String accessToken = jwtTokenProvider.generateAccessToken(testUser);
            String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

            // then
            assertThat(refreshToken).isNotEqualTo(accessToken);
        }

        @Test
        @DisplayName("should embed user ID as subject in the refresh token")
        void shouldEmbedUserIdInRefreshToken() {
            // when
            String token = jwtTokenProvider.generateRefreshToken(testUser);

            // then
            UUID extractedUserId = jwtTokenProvider.getUserIdFromToken(token);
            assertThat(extractedUserId).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("getUserIdFromToken()")
    class GetUserIdFromToken {

        @Test
        @DisplayName("should extract correct user ID from access token")
        void shouldExtractUserIdFromAccessToken() {
            // given
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // when
            UUID extractedId = jwtTokenProvider.getUserIdFromToken(token);

            // then
            assertThat(extractedId).isEqualTo(userId);
        }

        @Test
        @DisplayName("should extract correct user ID from refresh token")
        void shouldExtractUserIdFromRefreshToken() {
            // given
            String token = jwtTokenProvider.generateRefreshToken(testUser);

            // when
            UUID extractedId = jwtTokenProvider.getUserIdFromToken(token);

            // then
            assertThat(extractedId).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("getRoleFromToken()")
    class GetRoleFromToken {

        @Test
        @DisplayName("should extract BUYER role from access token")
        void shouldExtractBuyerRole() {
            // given
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // when
            String role = jwtTokenProvider.getRoleFromToken(token);

            // then
            assertThat(role).isEqualTo("BUYER");
        }

        @Test
        @DisplayName("should extract SELLER role from access token")
        void shouldExtractSellerRole() {
            // given
            testUser.setRole(UserRole.SELLER);
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // when
            String role = jwtTokenProvider.getRoleFromToken(token);

            // then
            assertThat(role).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("should extract ADMIN role from access token")
        void shouldExtractAdminRole() {
            // given
            testUser.setRole(UserRole.ADMIN);
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // when
            String role = jwtTokenProvider.getRoleFromToken(token);

            // then
            assertThat(role).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("validateToken()")
    class ValidateToken {

        @Test
        @DisplayName("should return true for a valid access token")
        void shouldReturnTrueForValidAccessToken() {
            // given
            String token = jwtTokenProvider.generateAccessToken(testUser);

            // when
            boolean isValid = jwtTokenProvider.validateToken(token);

            // then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("should return true for a valid refresh token")
        void shouldReturnTrueForValidRefreshToken() {
            // given
            String token = jwtTokenProvider.generateRefreshToken(testUser);

            // when
            boolean isValid = jwtTokenProvider.validateToken(token);

            // then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("should return false for a malformed token")
        void shouldReturnFalseForMalformedToken() {
            // when
            boolean isValid = jwtTokenProvider.validateToken("not.a.valid.jwt.token");

            // then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for a completely invalid string")
        void shouldReturnFalseForInvalidString() {
            // when
            boolean isValid = jwtTokenProvider.validateToken("random-garbage-string");

            // then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for an empty string")
        void shouldReturnFalseForEmptyString() {
            // when
            boolean isValid = jwtTokenProvider.validateToken("");

            // then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for a token signed with a different secret")
        void shouldReturnFalseForTokenWithDifferentSecret() {
            // given - create a provider with a different secret
            JwtTokenProvider otherProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(otherProvider, "secret", "ACompletelyDifferentSecretKeyThatIs32BytesLong!");
            ReflectionTestUtils.setField(otherProvider, "accessExpiration", ACCESS_EXPIRATION);
            ReflectionTestUtils.setField(otherProvider, "refreshExpiration", REFRESH_EXPIRATION);

            String tokenFromOtherProvider = otherProvider.generateAccessToken(testUser);

            // when - validate with the original provider
            boolean isValid = jwtTokenProvider.validateToken(tokenFromOtherProvider);

            // then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for an expired token")
        void shouldReturnFalseForExpiredToken() {
            // given - create a provider with negative expiration so token is already expired
            JwtTokenProvider expiredProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(expiredProvider, "secret", TEST_SECRET);
            ReflectionTestUtils.setField(expiredProvider, "accessExpiration", -1000L); // already expired
            ReflectionTestUtils.setField(expiredProvider, "refreshExpiration", REFRESH_EXPIRATION);

            String expiredToken = expiredProvider.generateAccessToken(testUser);

            // when
            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            // then
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("getAccessExpiration()")
    class GetAccessExpiration {

        @Test
        @DisplayName("should return the configured access token expiration")
        void shouldReturnConfiguredAccessExpiration() {
            // when
            long expiration = jwtTokenProvider.getAccessExpiration();

            // then
            assertThat(expiration).isEqualTo(ACCESS_EXPIRATION);
        }
    }
}
