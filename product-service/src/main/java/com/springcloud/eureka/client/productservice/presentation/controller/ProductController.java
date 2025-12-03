package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.ProductService;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RepProductDto>> createProduct(@Valid @RequestBody ReqProductCreateDto request, @RequestHeader("X-User-Username") String username) {

        RepProductDto response = productService.createProduct(username, request);

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
            @RequestBody ReqProductUpdateDto request,
            @RequestHeader("X-User-Username") String username
    ) {
        RepProductDto response = productService.updateProduct(productId, request, username);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 상품 상태변경 (판매완료)
    @PutMapping("/{productId}/status")
    public ResponseEntity<ApiResponse<RepProductDto>> updateProductStatus(
            @PathVariable UUID productId,
            @RequestBody ReqProductStatusUpdateDto request,
            @RequestHeader("X-User-Username") String username
    ) {
        RepProductDto response = productService.updateProductStatus(productId, request, username);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 상품 논리 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Username") String username
    ) {
        productService.deleteProduct(productId, username);
        Map<String, String> body = Map.of("result", "success");
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, body));
    }

}
