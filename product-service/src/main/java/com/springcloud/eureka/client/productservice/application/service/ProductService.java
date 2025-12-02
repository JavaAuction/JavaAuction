package com.springcloud.eureka.client.productservice.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.infrastructure.client.UserServiceClient;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserServiceClient userServiceClient;

    // 상품 생성
    public RepProductDto createProduct(String username, ReqProductCreateDto request){
        Product product = request.toEntity(username);
        product.setCreate(Instant.now(), username);
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
    public RepProductDto updateProduct(UUID productId, ReqProductUpdateDto request, String username) {
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

        product.setUpdated(Instant.now(), username);

        return RepProductDto.from(product);
    }

    // 상품 상태 변경 (판매 완료)
    public RepProductDto updateProductStatus(UUID productId, ReqProductStatusUpdateDto request, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // SOLD 등으로 상태 변경
        product.changeStatus(request.getProductStatus(), request.getFinalPrice());
        product.setUpdated(Instant.now(), username);

        return RepProductDto.from(product);
    }

    // 상품 논리 삭제
    public void deleteProduct(UUID productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.softDelete(Instant.now(), username);
    }

}
