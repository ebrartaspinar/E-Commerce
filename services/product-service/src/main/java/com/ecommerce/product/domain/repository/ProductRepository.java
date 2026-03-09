package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Page<Product> findBySellerId(UUID sellerId, Pageable pageable);

    Page<Product> findBySellerIdAndStatusNot(UUID sellerId, ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryAndStatus(Category category, ProductStatus status, Pageable pageable);
}
