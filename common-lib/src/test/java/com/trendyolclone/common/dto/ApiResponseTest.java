package com.trendyolclone.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse")
class ApiResponseTest {

    @Nested
    @DisplayName("ok() factory method")
    class OkMethod {

        @Test
        @DisplayName("should create a successful response with data and no message")
        void shouldCreateSuccessResponseWithDataOnly() {
            // given
            String data = "test-data";

            // when
            ApiResponse<String> response = ApiResponse.ok(data);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo("test-data");
            assertThat(response.message()).isNull();
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("should create a successful response with data and message")
        void shouldCreateSuccessResponseWithDataAndMessage() {
            // given
            Integer data = 42;
            String message = "Operation completed";

            // when
            ApiResponse<Integer> response = ApiResponse.ok(data, message);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(42);
            assertThat(response.message()).isEqualTo("Operation completed");
            assertThat(response.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("should handle null data gracefully")
        void shouldHandleNullData() {
            // when
            ApiResponse<Object> response = ApiResponse.ok(null);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isNull();
        }

        @Test
        @DisplayName("should accept complex object types as data")
        void shouldAcceptComplexObjectTypes() {
            // given
            record TestDto(String name, int value) {}
            TestDto dto = new TestDto("test", 99);

            // when
            ApiResponse<TestDto> response = ApiResponse.ok(dto, "Success");

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(dto);
            assertThat(response.data().name()).isEqualTo("test");
            assertThat(response.data().value()).isEqualTo(99);
        }
    }

    @Nested
    @DisplayName("error() factory method")
    class ErrorMethod {

        @Test
        @DisplayName("should create an error response with message and null data")
        void shouldCreateErrorResponseWithMessage() {
            // given
            String errorMessage = "Something went wrong";

            // when
            ApiResponse<Object> response = ApiResponse.error(errorMessage);

            // then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isEqualTo("Something went wrong");
            assertThat(response.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("should create an error response with null message")
        void shouldCreateErrorResponseWithNullMessage() {
            // when
            ApiResponse<Object> response = ApiResponse.error(null);

            // then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.message()).isNull();
        }

        @Test
        @DisplayName("should create an error response with empty message")
        void shouldCreateErrorResponseWithEmptyMessage() {
            // when
            ApiResponse<Object> response = ApiResponse.error("");

            // then
            assertThat(response.success()).isFalse();
            assertThat(response.message()).isEmpty();
        }
    }

    @Nested
    @DisplayName("timestamp field")
    class TimestampField {

        @Test
        @DisplayName("ok() should set timestamp close to current time")
        void okShouldSetTimestampCloseToCurrentTime() {
            // given
            LocalDateTime before = LocalDateTime.now();

            // when
            ApiResponse<String> response = ApiResponse.ok("data");

            // then
            LocalDateTime after = LocalDateTime.now();
            assertThat(response.timestamp()).isAfterOrEqualTo(before);
            assertThat(response.timestamp()).isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("error() should set timestamp close to current time")
        void errorShouldSetTimestampCloseToCurrentTime() {
            // given
            LocalDateTime before = LocalDateTime.now();

            // when
            ApiResponse<Object> response = ApiResponse.error("error");

            // then
            LocalDateTime after = LocalDateTime.now();
            assertThat(response.timestamp()).isAfterOrEqualTo(before);
            assertThat(response.timestamp()).isBeforeOrEqualTo(after);
        }
    }
}
