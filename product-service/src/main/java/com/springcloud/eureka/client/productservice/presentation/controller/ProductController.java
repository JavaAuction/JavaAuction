package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.PopularProductService;
import com.springcloud.eureka.client.productservice.application.service.ProductService;
import com.springcloud.eureka.client.productservice.application.service.ProductViewService;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import com.springcloud.eureka.client.productservice.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final PopularProductService popularProductService;
    private final ProductViewService productViewService;  // 추가!

    // 상품 등록
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RepProductDto>> createProduct(@RequestPart("request") ReqProductCreateDto request,
                                                                    @RequestPart(value = "file", required = false) MultipartFile file,
                                                                    @RequestHeader("X-User-Username") String username) {

        RepProductDto response = productService.createProduct(username, request, file);

        return ResponseEntity
                .status(BaseSuccessCode.CREATED.getStatus())
                .body(ApiResponse.success(BaseSuccessCode.CREATED, response));
    }

    // 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<RepProductDto>> getProduct(@PathVariable UUID productId) {

        // 1. 조회수 증가 (매번 실행!)
        productViewService.incrementViewCount(productId);

        // 2. 상품 조회 (캐시 활용!)
        RepProductDto dto = productService.getProduct(productId);

        // 3. 조회수 설정 (Redis에서 가져옴)
        Double viewCount = productViewService.getViewCount(productId);
        if (viewCount != null) {
            dto.setViewCount(viewCount.longValue());
        }

        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, dto));
    }

    // 상품 목록 조회
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

    // 인기 상품 목록 조회
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<RepProductDto>>> getPopularProducts() {
        List<RepProductDto> response = popularProductService.getPopularProducts();
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
    @PutMapping(value = "/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RepProductDto>> updateProduct(
            @PathVariable UUID productId,
            @RequestPart("request") ReqProductUpdateDto request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestHeader("X-User-Username") String username
    ) {
        RepProductDto response = productService.updateProduct(productId, request,file, username);
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
