package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqProductCreateDto {

    @NotBlank
    private String name;

    private String description;

    private  String imageUrl;

    @NotBlank
    private String categoryName;

    public Product toEntity(String userId) {
        return Product.create(userId, name, description, imageUrl, null);
    }
}
