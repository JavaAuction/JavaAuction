package com.springcloud.eureka.client.productservice.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import com.springcloud.eureka.client.productservice.domain.error.ProductErrorCode;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductCategoryRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryDto;
import com.springcloud.eureka.client.productservice.presentation.dto.RepCategoryListDto;
import com.springcloud.eureka.client.productservice.presentation.dto.ReqCategoryCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
}
