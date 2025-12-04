package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class RepCategoryDto {

    private String categoryId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

    public static RepCategoryDto from(ProductCategory category) {
        return RepCategoryDto.builder()
                .categoryId(category.getId().toString())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}