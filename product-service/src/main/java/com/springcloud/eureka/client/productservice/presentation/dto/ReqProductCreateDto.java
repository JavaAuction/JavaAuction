package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
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

    public Product toEntity(String userId) {
        return Product.create(
                userId,
                this.name,
                this.description,
                this.imageUrl
        );
    }
}
