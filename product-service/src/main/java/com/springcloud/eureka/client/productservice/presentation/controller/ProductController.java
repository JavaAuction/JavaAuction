package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.ProductService;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqProductCreateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RepProductCreateDto>> createProduct(@Valid @RequestBody ReqProductCreateDto request) {
        // Security 적용 후, 실제 유저 ID를 인증 컨텍스트에서 꺼내오기
        String mockUserId = "TEMP-USER";  // 현재는 임시 하드코딩

        RepProductCreateDto response = productService.createProduct(mockUserId, request);

        return ResponseEntity
                .status(BaseSuccessCode.CREATED.getStatus())
                .body(ApiResponse.success(BaseSuccessCode.CREATED, response));
    }
}
