package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepCategoryDto {

    private String categoryId;
    private String name;

    public static RepCategoryDto from(ProductCategory category) {
        return RepCategoryDto.builder()
                .categoryId(category.getId().toString())
                .name(category.getName())
                .build();
    }
}