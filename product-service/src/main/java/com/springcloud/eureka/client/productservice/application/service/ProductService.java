package com.springcloud.eureka.client.productservice.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductPageDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductUpdateDto;
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

    // 상품 목록 조회 (검색/상태, 카테고리는 나중에)
    @Transactional(readOnly = true)
    public RepProductPageDto getProducts(String keyword, ProductStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result;

        if (keyword == null || keyword.isBlank()) {
            if (status == null) {
                result = productRepository.findAll(pageable);
            } else {
                result = productRepository.findByStatus(status, pageable);
            }
        } else {
            if (status == null) {
                result = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
            } else {
                result = productRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
            }
        }

        Page<RepProductDto> mapped = result.map(RepProductDto::from);
        return RepProductPageDto.from(mapped);
    }

    // 상품명 조회
    @Transactional(readOnly = true)
    public String getProductName(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return product.getName();
    }

    // 상품 정보 수정
    public RepProductDto updateProduct(UUID productId, ReqProductUpdateDto request, String userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (request.getName() != null) {
            product.changeName(request.getName());
        }
        if (request.getDescription() != null) {
            product.changeDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            product.changeImageUrl(request.getImageUrl());
        }

        product.setUpdated(Instant.now(), userId);

        return RepProductDto.from(product);
    }

}
