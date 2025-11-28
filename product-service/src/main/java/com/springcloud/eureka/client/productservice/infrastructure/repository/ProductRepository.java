package com.springcloud.eureka.client.productservice.infrastructure.repository;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

}
