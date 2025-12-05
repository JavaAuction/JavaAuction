package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RepCategoryListDto {
    private List<RepCategoryDto> categories;

    public static RepCategoryListDto from(List<ProductCategory> list){
        List<RepCategoryDto> mapped = list.stream()
                .map(RepCategoryDto::from)
                .toList();
        return new RepCategoryListDto(mapped);
    }
}
