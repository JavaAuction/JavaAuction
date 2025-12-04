package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqCategoryCreateDto {

    @NotBlank
    private String name;

    public ProductCategory toEntity() {
        return ProductCategory.create(name);
    }

}
