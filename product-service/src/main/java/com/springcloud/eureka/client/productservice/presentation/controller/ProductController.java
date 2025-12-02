package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.ProductService;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductPageDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductUpdateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RepProductDto>> createProduct(@Valid @RequestBody ReqProductCreateDto request) {
        // Security 적용 후, 실제 유저 ID를 인증 컨텍스트에서 꺼내오기
        String mockUserId = "TEMP-USER";  // 현재는 임시 하드코딩

        RepProductDto response = productService.createProduct(mockUserId, request);

        return ResponseEntity
                .status(BaseSuccessCode.CREATED.getStatus())
                .body(ApiResponse.success(BaseSuccessCode.CREATED, response));
    }

    // 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<RepProductDto>> getProduct(
            @PathVariable UUID productId
    ) {
        RepProductDto response = productService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 상품 목록 조회 (검색/상태, 카테고리 제외)
    @GetMapping
    public ResponseEntity<ApiResponse<RepProductPageDto>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        RepProductPageDto response =
                productService.getProducts(keyword, status, page, size);

        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 상품명 조회
    @GetMapping("/{productId}/name")
    public ResponseEntity<ApiResponse<Map<String, String>>> getProductName(
            @PathVariable UUID productId
    ) {
        String name = productService.getProductName(productId);
        Map<String, String> response = Map.of("productName", name);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 상품 정보 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<RepProductDto>> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ReqProductUpdateDto request
    ) {
        String mockUserId = "TEMP-USER";
        RepProductDto response = productService.updateProduct(productId, request, mockUserId);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }
}
