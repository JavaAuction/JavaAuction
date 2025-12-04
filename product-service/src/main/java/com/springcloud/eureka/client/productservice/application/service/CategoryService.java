package com.springcloud.eureka.client.productservice.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductCategoryRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryListDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryCreateDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    // 카테고리 생성
    public RepCategoryDto createCategory(ReqCategoryCreateDto request, String adminUsername) {

        if (productCategoryRepository.existsByName(request.getName())) {
            throw new BussinessException(ProductErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        ProductCategory category = request.toEntity();
        category.setCreate(Instant.now(), adminUsername);

        ProductCategory saved = productCategoryRepository.save(category);
        return RepCategoryDto.from(saved);
    }

    // 카테고리 조회
    @Transactional(readOnly = true)
    public RepCategoryListDto getCategories() {
        List<ProductCategory> categories = productCategoryRepository.findAll();
        return RepCategoryListDto.from(categories);
    }

    // 카테고리 수정
    public RepCategoryDto updateCategory(UUID categoryId, ReqCategoryUpdateDto request, String adminUsername) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.CATEGORY_NOT_FOUND));

        if (productCategoryRepository.existsByName(request.getName())) {
            throw new BussinessException(ProductErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        category.changeName(request.getName());
        category.setUpdated(Instant.now(), adminUsername);

        return RepCategoryDto.from(category);
    }

    // 카테고리 삭제
    public void deleteCategory(UUID categoryId, String adminUsername) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BussinessException(ProductErrorCode.CATEGORY_NOT_FOUND));

        category.softDelete(Instant.now(), adminUsername);
    }

}
