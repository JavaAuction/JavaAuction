package com.springcloud.eureka.client.productservice.presentation.dto;

import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class RepProductDto {
    private UUID productId;
    private String userId;
    private String name;
    private String description;
    private String imageUrl;
    private Long finalPrice;
    private ProductStatus productStatus;
    private UUID categoryId;
    private String categoryName;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public static RepProductDto from(Product product) {
        return RepProductDto.builder()
                .productId(product.getId())
                .userId(product.getUserId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .finalPrice(product.getFinalPrice())
                .productStatus(product.getStatus())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategoryName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .build();
    }
}