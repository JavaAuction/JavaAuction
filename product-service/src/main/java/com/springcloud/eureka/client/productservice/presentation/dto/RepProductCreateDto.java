package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class RepProductCreateDto {
    private UUID productId;
    private String userId;
    private String name;
    private String description;
    private String imageUrl;
    private Long finalPrice;
    private ProductStatus productStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public static RepProductCreateDto from(Product product) {
        return RepProductCreateDto.builder()
                .productId(product.getId())
                .userId(product.getUserId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .finalPrice(product.getFinalPrice())
                .productStatus(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .build();
    }
}