package com.springcloud.eureka.client.productservice.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // 상품 생성
    public RepProductDto createProduct(String userId, ReqProductCreateDto request){
        Product product = request.toEntity(userId);
        product.setCreate(Instant.now(), userId);
        Product saved = productRepository.save(product);
        return RepProductDto.from(saved);
    }

    // 상품 단건 조회
    public RepProductDto getProduct(UUID productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return RepProductDto.from(product);
    }


}
