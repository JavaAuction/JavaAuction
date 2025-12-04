package com.springcloud.eureka.client.productservice.infrastructure.repository;

import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    boolean existsByName(String name);
    Optional<ProductCategory> findByName(String name);
}
