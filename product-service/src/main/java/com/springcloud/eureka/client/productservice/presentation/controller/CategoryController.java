package com.springcloud.eureka.client.productservice.presentation.controller;

import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.response.ApiResponse;
import com.springcloud.eureka.client.productservice.application.service.CategoryService;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryListDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "상품 카테고리 관리", description = "상품 카테고리 CRUD")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성 (ADMIN 전용)
    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 등록합니다. 관리자만 등록 가능합니다.")
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
    @Operation(summary = "카테고리 목록 조회", description = "카테고리 전체 목록을 조회합니다. 누구나 조회가능합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<RepCategoryListDto>> getCategories() {
        RepCategoryListDto response = categoryService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 수정 (ADMIN 전용)
    @Operation(summary = "카테고리 수정", description = "카테고리를 수정합니다. 관리자만 수정 가능합니다.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<RepCategoryDto>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody ReqCategoryUpdateDto request,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new BussinessException(ProductErrorCode.FORBIDDEN);
        }

        RepCategoryDto response = categoryService.updateCategory(categoryId, request, username);
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, response));
    }

    // 삭제 (ADMIN 전용, 논리 삭제 후 result: success)
    @Operation(summary = "카테고리 삭제", description = "카테고리를 수정합니다. 관리자만 삭제 가능합니다.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteCategory(
            @PathVariable UUID categoryId,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new BussinessException(ProductErrorCode.FORBIDDEN);
        }

        categoryService.deleteCategory(categoryId, username);
        Map<String, String> body = Map.of("result", "success");
        return ResponseEntity.ok(ApiResponse.success(BaseSuccessCode.OK, body));
    }

}
