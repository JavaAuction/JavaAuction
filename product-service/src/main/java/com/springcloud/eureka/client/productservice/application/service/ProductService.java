package com.springcloud.eureka.client.productservice.application.service;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductCreateDto;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // 상품 생성
    public RepProductCreateDto createProduct(String userId, ReqProductCreateDto request){
        Product product = request.toEntity(userId);
        product.setCreate(Instant.now(), userId);
        Product saved = productRepository.save(product);
        return RepProductCreateDto.from(saved);
    }

}
