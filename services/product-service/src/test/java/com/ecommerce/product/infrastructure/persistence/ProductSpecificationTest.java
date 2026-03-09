package com.ecommerce.product.infrastructure.persistence;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductSpecification Unit Tests")
class ProductSpecificationTest {

    @Nested
    @DisplayName("hasCategoryId")
    class HasCategoryId {

        @Test
        @DisplayName("Should create non-null specification for category ID")
        void shouldCreateNonNullSpecificationForCategoryId() {
            // given
            UUID categoryId = UUID.randomUUID();

            // when
            Specification<Product> spec = ProductSpecification.hasCategoryId(categoryId);

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasBrand")
    class HasBrand {

        @Test
        @DisplayName("Should create non-null specification for brand")
        void shouldCreateNonNullSpecificationForBrand() {
            // when
            Specification<Product> spec = ProductSpecification.hasBrand("Samsung");

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasPriceRange")
    class HasPriceRange {

        @Test
        @DisplayName("Should create non-null specification for price range with both min and max")
        void shouldCreateNonNullSpecificationForPriceRangeWithBothMinAndMax() {
            // when
            Specification<Product> spec = ProductSpecification.hasPriceRange(
                    new BigDecimal("10.00"), new BigDecimal("100.00"));

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null specification for price range with only min")
        void shouldCreateNonNullSpecificationForPriceRangeWithOnlyMin() {
            // when
            Specification<Product> spec = ProductSpecification.hasPriceRange(
                    new BigDecimal("10.00"), null);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null specification for price range with only max")
        void shouldCreateNonNullSpecificationForPriceRangeWithOnlyMax() {
            // when
            Specification<Product> spec = ProductSpecification.hasPriceRange(
                    null, new BigDecimal("100.00"));

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null specification for price range with both null")
        void shouldCreateNonNullSpecificationForPriceRangeWithBothNull() {
            // when
            Specification<Product> spec = ProductSpecification.hasPriceRange(null, null);

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasStatus")
    class HasStatus {

        @Test
        @DisplayName("Should create non-null specification for ACTIVE status")
        void shouldCreateNonNullSpecificationForActiveStatus() {
            // when
            Specification<Product> spec = ProductSpecification.hasStatus(ProductStatus.ACTIVE);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null specification for OUT_OF_STOCK status")
        void shouldCreateNonNullSpecificationForOutOfStockStatus() {
            // when
            Specification<Product> spec = ProductSpecification.hasStatus(ProductStatus.OUT_OF_STOCK);

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasSearchTerm")
    class HasSearchTerm {

        @Test
        @DisplayName("Should create non-null specification for search term")
        void shouldCreateNonNullSpecificationForSearchTerm() {
            // when
            Specification<Product> spec = ProductSpecification.hasSearchTerm("laptop");

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should create non-null specification for search term with special characters")
        void shouldCreateNonNullSpecificationForSearchTermWithSpecialCharacters() {
            // when
            Specification<Product> spec = ProductSpecification.hasSearchTerm("iPhone 15 Pro");

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("isNotDeleted")
    class IsNotDeleted {

        @Test
        @DisplayName("Should create non-null specification for not deleted filter")
        void shouldCreateNonNullSpecificationForNotDeleted() {
            // when
            Specification<Product> spec = ProductSpecification.isNotDeleted();

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("Specification Composition")
    class SpecificationComposition {

        @Test
        @DisplayName("Should compose multiple specifications together")
        void shouldComposeMultipleSpecifications() {
            // when
            Specification<Product> spec = Specification
                    .where(ProductSpecification.isNotDeleted())
                    .and(ProductSpecification.hasBrand("Apple"))
                    .and(ProductSpecification.hasPriceRange(new BigDecimal("500"), new BigDecimal("2000")))
                    .and(ProductSpecification.hasStatus(ProductStatus.ACTIVE))
                    .and(ProductSpecification.hasSearchTerm("macbook"));

            // then
            assertThat(spec).isNotNull();
        }
    }
}
