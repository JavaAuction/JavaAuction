package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.CategoryService;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryListDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성 (ADMIN 전용)
    @PostMapping
    public ResponseEntity<ApiResponse<RepCategoryDto>> createCategory(
            @Valid @RequestBody ReqCategoryCreateDto request,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {
        // role 이 ADMIN 인지 간단 체크 (필요 시 별도 권한 체크 컴포넌트로 분리 가능)
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new com.javaauction.global.presentation.exception.BussinessException(
                    ProductErrorCode.FORBIDDEN
            );
        }

        RepCategoryDto response = categoryService.createCategory(request, username);
        return ResponseEntity
                .status(BaseSuccessCode.CREATED.getStatus())
                .body(ApiResponse.success(BaseSuccessCode.CREATED, response));
    }

    // 목록 조회 (누구나)
    @GetMapping
    public ResponseEntity<ApiResponse<RepCategoryListDto>> getCategories() {
        RepCategoryListDto response = categoryService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }
}
