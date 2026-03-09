package com.ecommerce.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleResourceNotFound()")
    class HandleResourceNotFound {

        @Test
        @DisplayName("should return 404 NOT_FOUND with correct problem detail")
        void shouldReturnNotFoundStatus() {
            // given
            ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", "123");

            // when
            ProblemDetail problem = handler.handleResourceNotFound(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
            assertThat(problem.getDetail()).isEqualTo("User not found with id: '123'");
            assertThat(problem.getProperties()).containsKey("timestamp");
        }

        @Test
        @DisplayName("should include resource details in message")
        void shouldIncludeResourceDetailsInMessage() {
            // given
            ResourceNotFoundException ex = new ResourceNotFoundException("Product", "sku", "ABC-123");

            // when
            ProblemDetail problem = handler.handleResourceNotFound(ex);

            // then
            assertThat(problem.getDetail()).contains("Product", "sku", "ABC-123");
        }
    }

    @Nested
    @DisplayName("handleBusinessRule()")
    class HandleBusinessRule {

        @Test
        @DisplayName("should return 422 UNPROCESSABLE_ENTITY with correct problem detail")
        void shouldReturnUnprocessableEntityStatus() {
            // given
            BusinessRuleException ex = new BusinessRuleException("Invalid credentials");

            // when
            ProblemDetail problem = handler.handleBusinessRule(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
            assertThat(problem.getTitle()).isEqualTo("Business Rule Violation");
            assertThat(problem.getDetail()).isEqualTo("Invalid credentials");
            assertThat(problem.getProperties()).containsKey("timestamp");
        }
    }

    @Nested
    @DisplayName("handleDuplicateResource()")
    class HandleDuplicateResource {

        @Test
        @DisplayName("should return 409 CONFLICT with correct problem detail")
        void shouldReturnConflictStatus() {
            // given
            DuplicateResourceException ex = new DuplicateResourceException("User with email 'test@test.com' already exists");

            // when
            ProblemDetail problem = handler.handleDuplicateResource(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(problem.getTitle()).isEqualTo("Duplicate Resource");
            assertThat(problem.getDetail()).isEqualTo("User with email 'test@test.com' already exists");
            assertThat(problem.getProperties()).containsKey("timestamp");
        }
    }

    @Nested
    @DisplayName("handleValidation()")
    class HandleValidation {

        @Test
        @DisplayName("should return 400 BAD_REQUEST with field errors map")
        @SuppressWarnings("unchecked")
        void shouldReturnBadRequestWithFieldErrors() throws NoSuchMethodException {
            // given
            Object target = new Object();
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
            bindingResult.addError(new FieldError("request", "email", "Email is required"));
            bindingResult.addError(new FieldError("request", "password", "Password must be at least 8 characters"));

            MethodParameter methodParameter = new MethodParameter(
                    this.getClass().getDeclaredMethod("shouldReturnBadRequestWithFieldErrors"), -1
            );

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

            // when
            ProblemDetail problem = handler.handleValidation(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(problem.getTitle()).isEqualTo("Validation Error");
            assertThat(problem.getDetail()).isEqualTo("Validation failed");
            assertThat(problem.getProperties()).containsKey("errors");
            assertThat(problem.getProperties()).containsKey("timestamp");

            Map<String, String> errors = (Map<String, String>) problem.getProperties().get("errors");
            assertThat(errors).hasSize(2);
            assertThat(errors).containsEntry("email", "Email is required");
            assertThat(errors).containsEntry("password", "Password must be at least 8 characters");
        }
    }

    @Nested
    @DisplayName("handleIllegalArgument()")
    class HandleIllegalArgument {

        @Test
        @DisplayName("should return 400 BAD_REQUEST with correct problem detail")
        void shouldReturnBadRequestStatus() {
            // given
            IllegalArgumentException ex = new IllegalArgumentException("Invalid page number");

            // when
            ProblemDetail problem = handler.handleIllegalArgument(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(problem.getTitle()).isEqualTo("Bad Request");
            assertThat(problem.getDetail()).isEqualTo("Invalid page number");
            assertThat(problem.getProperties()).containsKey("timestamp");
        }
    }

    @Nested
    @DisplayName("handleGeneral()")
    class HandleGeneral {

        @Test
        @DisplayName("should return 500 INTERNAL_SERVER_ERROR with generic message")
        void shouldReturnInternalServerErrorStatus() {
            // given
            Exception ex = new RuntimeException("Unexpected error occurred in database layer");

            // when
            ProblemDetail problem = handler.handleGeneral(ex);

            // then
            assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
            assertThat(problem.getDetail()).isEqualTo("An unexpected error occurred");
            assertThat(problem.getProperties()).containsKey("timestamp");
        }

        @Test
        @DisplayName("should not leak internal exception details in the response")
        void shouldNotLeakInternalDetails() {
            // given
            Exception ex = new NullPointerException("some.internal.Class.method(Class.java:42)");

            // when
            ProblemDetail problem = handler.handleGeneral(ex);

            // then
            assertThat(problem.getDetail()).doesNotContain("NullPointerException");
            assertThat(problem.getDetail()).doesNotContain("some.internal.Class");
            assertThat(problem.getDetail()).isEqualTo("An unexpected error occurred");
        }
    }
}
