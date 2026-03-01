package com.trendyolclone.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PagedResponse")
class PagedResponseTest {

    @Test
    @DisplayName("should correctly map Spring Page to PagedResponse with content")
    void shouldMapPageToPagedResponseWithContent() {
        // given
        List<String> content = List.of("item1", "item2", "item3");
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<String> springPage = new PageImpl<>(content, pageRequest, 3);

        // when
        PagedResponse<String> response = PagedResponse.from(springPage);

        // then
        assertThat(response.content()).containsExactly("item1", "item2", "item3");
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(3);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.last()).isTrue();
        assertThat(response.first()).isTrue();
    }

    @Test
    @DisplayName("should correctly map empty Spring Page to PagedResponse")
    void shouldMapEmptyPageToPagedResponse() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<String> emptyPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        // when
        PagedResponse<String> response = PagedResponse.from(emptyPage);

        // then
        assertThat(response.content()).isEmpty();
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
        assertThat(response.last()).isTrue();
        assertThat(response.first()).isTrue();
    }

    @Test
    @DisplayName("should correctly set first and last flags for middle page")
    void shouldSetFirstAndLastFlagsForMiddlePage() {
        // given
        List<String> content = List.of("item4", "item5", "item6");
        PageRequest pageRequest = PageRequest.of(1, 3);
        Page<String> middlePage = new PageImpl<>(content, pageRequest, 9); // 3 pages total, we're on page 1 (middle)

        // when
        PagedResponse<String> response = PagedResponse.from(middlePage);

        // then
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.totalElements()).isEqualTo(9);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
    }

    @Test
    @DisplayName("should correctly set last flag for last page")
    void shouldSetLastFlagForLastPage() {
        // given
        List<String> content = List.of("item7");
        PageRequest pageRequest = PageRequest.of(2, 3);
        Page<String> lastPage = new PageImpl<>(content, pageRequest, 7); // 3 pages total, page 2 is last

        // when
        PagedResponse<String> response = PagedResponse.from(lastPage);

        // then
        assertThat(response.page()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    @Test
    @DisplayName("should correctly set first flag for first page with multiple pages")
    void shouldSetFirstFlagForFirstPageWithMultiplePages() {
        // given
        List<Integer> content = List.of(1, 2, 3);
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<Integer> firstPage = new PageImpl<>(content, pageRequest, 10);

        // when
        PagedResponse<Integer> response = PagedResponse.from(firstPage);

        // then
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.totalPages()).isEqualTo(4);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
    }

    @Test
    @DisplayName("should preserve content types when mapping complex objects")
    void shouldPreserveContentTypesForComplexObjects() {
        // given
        record ProductDto(String name, double price) {}
        List<ProductDto> products = List.of(
                new ProductDto("Phone", 999.99),
                new ProductDto("Laptop", 1499.99)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDto> productPage = new PageImpl<>(products, pageRequest, 2);

        // when
        PagedResponse<ProductDto> response = PagedResponse.from(productPage);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).name()).isEqualTo("Phone");
        assertThat(response.content().get(1).price()).isEqualTo(1499.99);
    }
}
